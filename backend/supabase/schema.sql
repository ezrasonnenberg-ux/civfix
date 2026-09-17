-- Enable UUID extension
create extension if not exists "uuid-ossp";

-- 1. Profiles Table (Linked to Supabase Auth)
create table public.profiles (
    id uuid references auth.users on delete cascade primary key,
    full_name text,
    avatar_url text,
    role text default 'citizen' check (role in ('citizen', 'municipal_worker')),
    created_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- 2. Issues Table
create table public.issues (
    id uuid default uuid_generate_v4() primary key,
    user_id uuid references public.profiles(id) on delete cascade not null,
    title text not null,
    category text not null check (category in ('Pothole / Road', 'Streetlight', 'Sanitation', 'Graffiti', 'Water / Drain', 'Park / Trees')),
    description text not null,
    severity text not null check (severity in ('Low', 'Medium', 'High', 'Critical')),
    status text default 'Pending' check (status in ('Pending', 'Scheduled', 'In Progress', 'Resolved')),
    latitude double precision not null,
    longitude double precision not null,
    address_text text not null,
    image_urls text[] default array[]::text[],
    upvotes_count integer default 0,
    created_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- 3. Issue Upvotes Table (Prevents duplicate upvotes)
create table public.issue_upvotes (
    id uuid default uuid_generate_v4() primary key,
    issue_id uuid references public.issues(id) on delete cascade not null,
    user_id uuid references public.profiles(id) on delete cascade not null,
    created_at timestamp with time zone default timezone('utc'::text, now()) not null,
    constraint unique_user_issue_upvote unique (issue_id, user_id)
);

-- Enable Row Level Security (RLS)
alter table public.profiles enable row level security;
alter table public.issues enable row level security;
alter table public.issue_upvotes enable row level security;

-- Basic RLS Policies (Allow public read, authenticated write)
create policy "Public profiles are viewable by everyone." on public.profiles for select using (true);
create policy "Users can update their own profile." on public.profiles for update using (auth.uid() = id);

create policy "Issues are viewable by everyone." on public.issues for select using (true);
create policy "Authenticated users can create issues." on public.issues for insert with check (auth.role() = 'authenticated');

create policy "Upvotes are viewable by everyone." on public.issue_upvotes for select using (true);
create policy "Authenticated users can manage their upvotes." on public.issue_upvotes for all using (auth.uid() = user_id);