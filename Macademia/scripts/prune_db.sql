delete from person_interests where person_id > 20;
delete from person where id > 20;
drop table foo if exists;
select id into foo from interest minus (select interest_id as id from person_interests);
delete from interest_relation where first_id in (select id from foo) or second_id in (select id from foo);
delete from interest where id in (select id from foo);
