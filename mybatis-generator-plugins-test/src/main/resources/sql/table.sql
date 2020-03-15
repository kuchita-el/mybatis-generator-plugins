create table member
(
	member_id VARCHAR(255) not null
		constraint member_pk
			primary key,
	name VARCHAR(255) not null,
	created_at timestamp default now() not null,
	updated_at timestamp default now() not null
);
