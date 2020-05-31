--
-- PostgreSQL database dump
--

-- Dumped from database version 12.2
-- Dumped by pg_dump version 12.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';



--
-- Name: dropAppointment(bigint); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public."dropAppointment"(a_id bigint)
    LANGUAGE plpgsql
    AS $$begin

delete from "ServiceInstance"
where appointment_id = a_id;

delete from "Appointment" as A
where A.id = a_id;

end;$$;


ALTER PROCEDURE public."dropAppointment"(a_id bigint) OWNER TO postgres;

--
-- Name: dropAppointmentsInDateRange(bigint, timestamp without time zone, timestamp without time zone); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public."dropAppointmentsInDateRange"(w_id bigint, date_since timestamp without time zone, date_to timestamp without time zone)
    LANGUAGE plpgsql
    AS $$declare

appointments bigint[];
counter integer := 1;
len integer := array_length(appointments, 1);

begin

select SI.appointment_id
from "Appointment" as A
join "ServiceInstance" as SI on SI.appointment_id = A.id
where A.job_id = w_id and SI.date between date_since and date_to
group by SI.appointment_id
into appointments;

LOOP EXIT WHEN counter = len;
	CALL dropAppointment(appointments[counter]);
	counter := counter + 1;
END LOOP;
end;$$;


ALTER PROCEDURE public."dropAppointmentsInDateRange"(w_id bigint, date_since timestamp without time zone, date_to timestamp without time zone) OWNER TO postgres;

--
-- Name: dropJob(bigint); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public."dropJob"(j_id bigint)
    LANGUAGE plpgsql
    AS $$begin

delete from "Provides"
where job_id = j_id;

delete from "Job"
where id = j_id;

end;$$;


ALTER PROCEDURE public."dropJob"(j_id bigint) OWNER TO postgres;

--
-- Name: dropPlace(bigint); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public."dropPlace"(p_id bigint)
    LANGUAGE plpgsql
    AS $$begin


delete from "Provides" as P
where P.job_id in 
(select job_id
from "Job" as J
where J.id = P.work_id and J.place_id = p_id);

delete from "Job" where place_id = p_id;

delete from "Place" where id = p_id;

end;$$;


ALTER PROCEDURE public."dropPlace"(p_id bigint) OWNER TO postgres;

--
-- Name: dropPromotion(bigint); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public."dropPromotion"(p_id bigint)
    LANGUAGE plpgsql
    AS $$begin
-- may want to notify price change or dont allow drop if there are already appointments
-- with this promotion
delete from "promoIncludes" where promotion_id = p_id;
delete from "Promotion" where id = p_id;

end$$;


ALTER PROCEDURE public."dropPromotion"(p_id bigint) OWNER TO postgres;

--
-- Name: getAppointments(bigint, date, bigint[]); Type: FUNCTION; Schema: public; Owner: ezegi
--

CREATE FUNCTION public."getAppointments"(w_id bigint, d date, services bigint[]) RETURNS TABLE(appointment_id bigint, service_id bigint, date timestamp without time zone)
    LANGUAGE plpgsql
    AS $$begin

return query
select A.id, SI.service_id, SI.date
from "Appointment" as A
join "ServiceInstance" as SI on A.id = SI.appointment_id
where A.job_id = w_id and A.date = d and SI.service_id in
			(select *
			from "restauth_customuser" as SP
			where SP.isProvider and SP.id in (select * from unnest(services)))
order by A.id;

end;$$;


ALTER FUNCTION public."getAppointments"(w_id bigint, d date, services bigint[]) OWNER TO ezegi;

--
-- Name: getClientFrequency(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public."getClientFrequency"(w_id bigint) RETURNS TABLE(client_id bigint, cant_appointments bigint)
    LANGUAGE plpgsql
    AS $$begin

return query
select A.client_id, count(id)
from "Appointment" as A
where A.job_id = w_id
group by A.client_id;

end;$$;


ALTER FUNCTION public."getClientFrequency"(w_id bigint) OWNER TO postgres;

--
-- Name: getDoableServicesFromPlace(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public."getDoableServicesFromPlace"(p_id bigint) RETURNS TABLE(id bigint, name character varying, jobtype_type character varying)
    LANGUAGE plpgsql
    AS $$BEGIN
    return query select S.id, S.name, S.jobtype_type
	from "PlaceDoes" as PD
	join "Service" as S on PD.jobtype_type = S.jobtype_type
	where place_id = p_id;

END;
$$;


ALTER FUNCTION public."getDoableServicesFromPlace"(p_id bigint) OWNER TO postgres;

--
-- Name: getPromotions(bigint, date); Type: FUNCTION; Schema: public; Owner: ezegi
--

CREATE FUNCTION public."getPromotions"(w_id bigint, d date) RETURNS TABLE(promotion_id bigint, service_id bigint, discount double precision, days integer[])
    LANGUAGE plpgsql
    AS $$/*
	Returns Promotions for a given date and work
*/

begin

return query 
select P.id, PI.service_id, PI.discount, P.days
from "Promotion" as P
join "promoIncludes" as PI on PI.promotion_id = P.id
where P.job_id = w_id and d between P.since and P.to and extract(dow from d) in (
	select *
	from unnest(P.days)
);

end;$$;


ALTER FUNCTION public."getPromotions"(w_id bigint, d date) OWNER TO ezegi;

--
-- Name: newAppointment(bigint, bigint, bigint[], timestamp without time zone[]); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public."newAppointment"(w_id bigint, c_id bigint, services bigint[], services_time timestamp without time zone[]) RETURNS integer
    LANGUAGE plpgsql
    AS $$/*

Inserts a new Appointment
and applies a promotion if available

*/
declare

len INTEGER := array_length(services, 1) + 1;
counter INTEGER := 1;
new_app_id bigint;

begin
CREATE TEMP TABLE promotions ON COMMIT DROP as
	select * from "getPromotions"(w_id, services_time[1]::date);

-- appointment insertion
INSERT INTO "Appointment" values (DEFAULT, w_id, c_id, services_time[1]::date);
SELECT currval('"Appointment_id_seq"') into new_app_id;

-- serviceInstance insertions
LOOP EXIT WHEN counter = len;
	
	INSERT INTO "ServiceInstance" values (new_app_id,
										  services_time[counter],
										  services[counter]);
	--applying promotions
	IF EXISTS (select * from promotions where service_id = services[counter]) THEN
		insert into "promoApplied" values(
			(select promotion_id from promotions where service_id = services[counter]),
			new_app_id,
			services[counter]
		);
	END IF;
	
	counter := counter + 1;
END LOOP;



return new_app_id;
end;$$;


ALTER FUNCTION public."newAppointment"(w_id bigint, c_id bigint, services bigint[], services_time timestamp without time zone[]) OWNER TO postgres;