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
-- Name: cantParallelismService(bigint, bigint, timestamp without time zone); Type: FUNCTION; Schema: public; Owner: ezegi
--

CREATE FUNCTION public."cantParallelismService"(w_id bigint, s_id bigint, date timestamp without time zone) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$/*
	returns how many appointments there are for a given service and timestamp
*/
declare

	service_duration time(4);
	o bigint;
	
begin

select duration
from "Provides" as P
where P.work_id = w_id and P.service_id = s_id and day = extract(dow from date)
into service_duration;

select count(*)
from "Appointment" as A
join "ServiceInstance" as S on S.appointment_id = A.id
where A.work_id = w_id and S.service_id = s_id and $3 between S.date and (S.date + service_duration) into o;

RETURN o;

end$_$;


ALTER FUNCTION public."cantParallelismService"(w_id bigint, s_id bigint, date timestamp without time zone) OWNER TO ezegi;

--
-- Name: checkPromo(bigint, bigint); Type: FUNCTION; Schema: public; Owner: ezegi
--

CREATE FUNCTION public."checkPromo"(p_id bigint, s_id bigint) RETURNS boolean
    LANGUAGE plpgsql
    AS $$/*
	Promotion insertion control
	The given service must be provided by the service provider on the given days
*/
declare

days_of_promo integer[];
len integer;
counter integer := 1;
w_id bigint;

begin

select work_id
from "Promotion" as P
where P.id = p_id
into w_id;

select days
from "Promotion" as P
where P.id = p_id
into days_of_promo;

select array_length(days_of_promo, 1) + 1 into len;

LOOP EXIT WHEN counter = len;
	
	IF "checkProvidesDay"(w_id, s_id, days_of_promo[counter]) = FALSE THEN
		RETURN FALSE;
	END IF;

	counter := counter + 1;
END LOOP;
RETURN TRUE;

end;$$;


ALTER FUNCTION public."checkPromo"(p_id bigint, s_id bigint) OWNER TO ezegi;

--
-- Name: checkProvides(bigint, bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public."checkProvides"(w_id bigint, s_id bigint) RETURNS boolean
    LANGUAGE plpgsql
    AS $$/*
Checks whether a service belongs to a jobtype
provided in the place where work is done
*/
declare
service_jobtype character varying;

begin

select S.jobtype_type
from "Service" as S
where S.id = s_id
into service_jobtype;

IF exists(
		  (	select jobtype_type
			from "Work" as w
			join "PlaceDoes" as P on w.place_id = P.place_id
			where w.id = w_id and P.jobtype_type = service_jobtype)
		 ) THEN
	RETURN TRUE;
ELSE
	RETURN FALSE;
END IF;
end;$$;


ALTER FUNCTION public."checkProvides"(w_id bigint, s_id bigint) OWNER TO postgres;

--
-- Name: checkProvidesDay(bigint, bigint, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public."checkProvidesDay"(w_id bigint, s_id bigint, dayofweek integer) RETURNS boolean
    LANGUAGE plpgsql
    AS $$/*
Checks whether a given service corresponds to a jobtype done at the works place
and 
whether a given service is done at a given day
*/
begin
IF "checkProvides"(w_id, s_id) and

	exists(	select *
			from "Provides" as P
			where P.work_id = w_id and service_id = s_id and day = dayofweek)

THEN
	RETURN TRUE;
ELSE
	RETURN FALSE;
END IF;

end;$$;


ALTER FUNCTION public."checkProvidesDay"(w_id bigint, s_id bigint, dayofweek integer) OWNER TO postgres;

--
-- Name: checkServiceInstance(bigint, bigint, timestamp without time zone); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public."checkServiceInstance"(a_id bigint, s_id bigint, date timestamp without time zone) RETURNS boolean
    LANGUAGE plpgsql
    AS $$declare

w_id bigint;
parallelism integer;

begin

select work_id
from "Appointment" as A
where A.id = a_id into w_id;

select max_parallelism
from "Provides" as P
where P.work_id = w_id and P.service_id = s_id and day = extract(dow from date)
into parallelism;

IF 
	"checkProvidesDay"(w_id, s_id, extract(dow from date)::integer)
	and
	"cantParallelismService"(w_id, s_id, date) < parallelism
THEN
	RETURN TRUE;
ELSE
	RETURN FALSE;
END IF;
end;$$;


ALTER FUNCTION public."checkServiceInstance"(a_id bigint, s_id bigint, date timestamp without time zone) OWNER TO postgres;

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
where A.work_id = w_id and SI.date between date_since and date_to
group by SI.appointment_id
into appointments;

LOOP EXIT WHEN counter = len;
	CALL dropAppointment(appointments[counter]);
	counter := counter + 1;
END LOOP;
end;$$;


ALTER PROCEDURE public."dropAppointmentsInDateRange"(w_id bigint, date_since timestamp without time zone, date_to timestamp without time zone) OWNER TO postgres;

--
-- Name: dropPlace(bigint); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public."dropPlace"(p_id bigint)
    LANGUAGE plpgsql
    AS $$begin


delete from "Provides" as P
where P.work_id in 
(select work_id
from "Work" as W
where W.id = P.work_id and W.place_id = p_id);

delete from "Work" where place_id = p_id;

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
-- Name: dropWork(bigint); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public."dropWork"(w_id bigint)
    LANGUAGE plpgsql
    AS $$begin

delete from "Provides"
where work_id = w_id;

delete from Work
where id = w_id;

end;$$;


ALTER PROCEDURE public."dropWork"(w_id bigint) OWNER TO postgres;

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
where A.work_id = w_id and A.date = d and SI.service_id in
			(select *
			from "ServiceProvider" as SP
			where SP.id in (select * from unnest(services)))
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
where A.work_id = w_id
group by A.client_id;

end;$$;


ALTER FUNCTION public."getClientFrequency"(w_id bigint) OWNER TO postgres;

--
-- Name: getClientId(character varying); Type: FUNCTION; Schema: public; Owner: ezegi
--

CREATE FUNCTION public."getClientId"(e character varying) RETURNS bigint
    LANGUAGE plpgsql
    AS $$declare
o bigint;
begin

select client_id
from "ClientAuth"
where email = e
into o;
return o;

end;$$;


ALTER FUNCTION public."getClientId"(e character varying) OWNER TO ezegi;

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
where P.work_id = w_id and d between P.since and P.to and extract(dow from d) in (
	select *
	from unnest(P.days)
);

end;$$;


ALTER FUNCTION public."getPromotions"(w_id bigint, d date) OWNER TO ezegi;

--
-- Name: getServiceProviderId(character varying); Type: FUNCTION; Schema: public; Owner: ezegi
--

CREATE FUNCTION public."getServiceProviderId"(e character varying) RETURNS bigint
    LANGUAGE plpgsql
    AS $$declare 

o bigint;

begin

select serviceprovider_id 
from "ServiceProviderAuth"
where email = e 
into o;
return o;

end;$$;


ALTER FUNCTION public."getServiceProviderId"(e character varying) OWNER TO ezegi;

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
			services_time[counter]
		);
	END IF;
	
	counter := counter + 1;
END LOOP;



return new_app_id;
end;$$;


ALTER FUNCTION public."newAppointment"(w_id bigint, c_id bigint, services bigint[], services_time timestamp without time zone[]) OWNER TO postgres;

--
-- Name: newClient(character varying, character varying); Type: FUNCTION; Schema: public; Owner: ezegi
--

CREATE FUNCTION public."newClient"(new_name character varying, new_email character varying) RETURNS bigint
    LANGUAGE plpgsql
    AS $$declare

new_client_id bigint;

begin

IF EXISTS (select * from "ClientAuth" where email = new_email) THEN
	RAISE EXCEPTION 'User email already in use';
ELSE
	insert into "Client" values (DEFAULT, new_name);
	select currval('"Client_id_seq"') into new_client_id;
	insert into "ClientAuth" values (new_client_id, new_email);
	return new_client_id;
END IF;

end;$$;


ALTER FUNCTION public."newClient"(new_name character varying, new_email character varying) OWNER TO ezegi;

--
-- Name: newPromotion(bigint, bigint[], double precision[], character varying, date, date, integer[]); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public."newPromotion"(w_id bigint, services bigint[], discounts double precision[], description character varying, date_since date, date_to date, days integer[])
    LANGUAGE plpgsql
    AS $$/*
	Creates a new Promotion
*/
declare

new_promo_id bigint;
counter integer = 1;
array_len integer = array_length(services, 1) + 1;

begin

insert into "Promotion" values (DEFAULT, w_id, date_since, date_to, description, days);
select currval('"Promotion_id_seq"') into new_promo_id;

LOOP EXIT WHEN counter = array_len;

	insert into "promoIncludes" values(new_promo_id, services[counter], discounts[counter]);
	counter := counter + 1;

END LOOP;

end;$$;


ALTER PROCEDURE public."newPromotion"(w_id bigint, services bigint[], discounts double precision[], description character varying, date_since date, date_to date, days integer[]) OWNER TO postgres;

--
-- Name: newServiceProvider(character varying, character varying, date, boolean); Type: FUNCTION; Schema: public; Owner: ezegi
--

CREATE FUNCTION public."newServiceProvider"(new_name character varying, new_email character varying, birth_date date, is_pro boolean DEFAULT false) RETURNS bigint
    LANGUAGE plpgsql
    AS $$declare

new_sp_id bigint;

begin

IF Exists(select email from "ServiceProviderAuth" as S where S.email = new_email) THEN
	RAISE EXCEPTION 'User email already in use';
ELSE
	INSERT INTO "ServiceProvider" values (DEFAULT, new_name, birth_date, is_pro);
	select currval('"ServiceProvider_id_seq"') into new_sp_id;
	INSERT INTO "ServiceProviderAuth" values (new_sp_id, email);
	RETURN new_sp_id;
END IF;
end;$$;


ALTER FUNCTION public."newServiceProvider"(new_name character varying, new_email character varying, birth_date date, is_pro boolean) OWNER TO ezegi;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: Appointment; Type: TABLE; Schema: public; Owner: ezegi
--

CREATE TABLE public."Appointment" (
    id bigint NOT NULL,
    work_id bigint NOT NULL,
    client_id bigint NOT NULL,
    date date NOT NULL,
    description text
);


ALTER TABLE public."Appointment" OWNER TO ezegi;

--
-- Name: Appointment_id_seq; Type: SEQUENCE; Schema: public; Owner: ezegi
--

CREATE SEQUENCE public."Appointment_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."Appointment_id_seq" OWNER TO ezegi;

--
-- Name: Appointment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ezegi
--

ALTER SEQUENCE public."Appointment_id_seq" OWNED BY public."Appointment".id;


--
-- Name: Client; Type: TABLE; Schema: public; Owner: ezegi
--

CREATE TABLE public."Client" (
    id bigint NOT NULL,
    name character varying NOT NULL
);


ALTER TABLE public."Client" OWNER TO ezegi;

--
-- Name: ClientAuth; Type: TABLE; Schema: public; Owner: ezegi
--

CREATE TABLE public."ClientAuth" (
    client_id bigint NOT NULL,
    email character varying NOT NULL
);


ALTER TABLE public."ClientAuth" OWNER TO ezegi;

--
-- Name: Client_id_seq; Type: SEQUENCE; Schema: public; Owner: ezegi
--

CREATE SEQUENCE public."Client_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."Client_id_seq" OWNER TO ezegi;

--
-- Name: Client_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ezegi
--

ALTER SEQUENCE public."Client_id_seq" OWNED BY public."Client".id;


--
-- Name: JobType; Type: TABLE; Schema: public; Owner: ezegi
--

CREATE TABLE public."JobType" (
    type character varying NOT NULL
);


ALTER TABLE public."JobType" OWNER TO ezegi;

--
-- Name: Notification; Type: TABLE; Schema: public; Owner: ezegi
--

CREATE TABLE public."Notification" (
    type character varying NOT NULL
);


ALTER TABLE public."Notification" OWNER TO ezegi;

--
-- Name: Place; Type: TABLE; Schema: public; Owner: ezegi
--

CREATE TABLE public."Place" (
    id bigint NOT NULL,
    serviceprovider_owner_id bigint NOT NULL,
    street character varying NOT NULL,
    streetnumber integer NOT NULL,
    apnumber integer NOT NULL,
    city character varying NOT NULL,
    state character varying NOT NULL,
    country character varying NOT NULL,
    businessname character varying NOT NULL,
    phonenumber character varying,
    email character varying NOT NULL
);


ALTER TABLE public."Place" OWNER TO ezegi;

--
-- Name: PlaceDoes; Type: TABLE; Schema: public; Owner: ezegi
--

CREATE TABLE public."PlaceDoes" (
    place_id bigint NOT NULL,
    jobtype_type character varying NOT NULL
);


ALTER TABLE public."PlaceDoes" OWNER TO ezegi;

--
-- Name: Place_id_seq; Type: SEQUENCE; Schema: public; Owner: ezegi
--

CREATE SEQUENCE public."Place_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."Place_id_seq" OWNER TO ezegi;

--
-- Name: Place_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ezegi
--

ALTER SEQUENCE public."Place_id_seq" OWNED BY public."Place".id;


--
-- Name: Prefers; Type: TABLE; Schema: public; Owner: ezegi
--

CREATE TABLE public."Prefers" (
    client_id bigint NOT NULL,
    notification_type character varying NOT NULL,
    contact_info character varying NOT NULL
);


ALTER TABLE public."Prefers" OWNER TO ezegi;

--
-- Name: Promotion; Type: TABLE; Schema: public; Owner: ezegi
--

CREATE TABLE public."Promotion" (
    id bigint NOT NULL,
    work_id bigint NOT NULL,
    since date NOT NULL,
    "to" date NOT NULL,
    description text,
    days integer[]
);


ALTER TABLE public."Promotion" OWNER TO ezegi;

--
-- Name: Promotion_id_seq; Type: SEQUENCE; Schema: public; Owner: ezegi
--

CREATE SEQUENCE public."Promotion_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."Promotion_id_seq" OWNER TO ezegi;

--
-- Name: Promotion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ezegi
--

ALTER SEQUENCE public."Promotion_id_seq" OWNED BY public."Promotion".id;


--
-- Name: Provides; Type: TABLE; Schema: public; Owner: ezegi
--

CREATE TABLE public."Provides" (
    work_id bigint NOT NULL,
    service_id bigint NOT NULL,
    day bigint NOT NULL,
    cost double precision NOT NULL,
    duration time(4) without time zone NOT NULL,
    day_start time(4) without time zone NOT NULL,
    day_end time(4) without time zone,
    pause_start time(4) without time zone,
    pause_end time(4) with time zone,
    max_parallelism integer NOT NULL
);


ALTER TABLE public."Provides" OWNER TO ezegi;

--
-- Name: Service; Type: TABLE; Schema: public; Owner: ezegi
--

CREATE TABLE public."Service" (
    id bigint NOT NULL,
    name character varying NOT NULL,
    jobtype_type character varying NOT NULL
);


ALTER TABLE public."Service" OWNER TO ezegi;

--
-- Name: ServiceInstance; Type: TABLE; Schema: public; Owner: ezegi
--

CREATE TABLE public."ServiceInstance" (
    appointment_id bigint NOT NULL,
    date timestamp without time zone NOT NULL,
    service_id bigint NOT NULL
);


ALTER TABLE public."ServiceInstance" OWNER TO ezegi;

--
-- Name: ServiceProvider; Type: TABLE; Schema: public; Owner: ezegi
--

CREATE TABLE public."ServiceProvider" (
    id integer NOT NULL,
    name character varying NOT NULL,
    birth_date date NOT NULL,
    is_pro boolean NOT NULL
);


ALTER TABLE public."ServiceProvider" OWNER TO ezegi;

--
-- Name: ServiceProviderAuth; Type: TABLE; Schema: public; Owner: ezegi
--

CREATE TABLE public."ServiceProviderAuth" (
    serviceprovider_id bigint NOT NULL,
    email character varying NOT NULL
);


ALTER TABLE public."ServiceProviderAuth" OWNER TO ezegi;

--
-- Name: ServiceProvider_id_seq; Type: SEQUENCE; Schema: public; Owner: ezegi
--

CREATE SEQUENCE public."ServiceProvider_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."ServiceProvider_id_seq" OWNER TO ezegi;

--
-- Name: ServiceProvider_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ezegi
--

ALTER SEQUENCE public."ServiceProvider_id_seq" OWNED BY public."ServiceProvider".id;


--
-- Name: Service_id_seq; Type: SEQUENCE; Schema: public; Owner: ezegi
--

CREATE SEQUENCE public."Service_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."Service_id_seq" OWNER TO ezegi;

--
-- Name: Service_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ezegi
--

ALTER SEQUENCE public."Service_id_seq" OWNED BY public."Service".id;


--
-- Name: Work; Type: TABLE; Schema: public; Owner: ezegi
--

CREATE TABLE public."Work" (
    id integer NOT NULL,
    serviceprovider_id bigint NOT NULL,
    place_id bigint NOT NULL
);


ALTER TABLE public."Work" OWNER TO ezegi;

--
-- Name: Work_id_seq; Type: SEQUENCE; Schema: public; Owner: ezegi
--

CREATE SEQUENCE public."Work_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."Work_id_seq" OWNER TO ezegi;

--
-- Name: Work_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ezegi
--

ALTER SEQUENCE public."Work_id_seq" OWNED BY public."Work".id;


--
-- Name: promoApplied; Type: TABLE; Schema: public; Owner: ezegi
--

CREATE TABLE public."promoApplied" (
    promotion_id bigint NOT NULL,
    appointment_id bigint NOT NULL,
    date timestamp without time zone NOT NULL
);


ALTER TABLE public."promoApplied" OWNER TO ezegi;

--
-- Name: promoIncludes; Type: TABLE; Schema: public; Owner: ezegi
--

CREATE TABLE public."promoIncludes" (
    promotion_id bigint NOT NULL,
    service_id bigint NOT NULL,
    discount double precision
);


ALTER TABLE public."promoIncludes" OWNER TO ezegi;

--
-- Name: Appointment id; Type: DEFAULT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Appointment" ALTER COLUMN id SET DEFAULT nextval('public."Appointment_id_seq"'::regclass);


--
-- Name: Client id; Type: DEFAULT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Client" ALTER COLUMN id SET DEFAULT nextval('public."Client_id_seq"'::regclass);


--
-- Name: Place id; Type: DEFAULT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Place" ALTER COLUMN id SET DEFAULT nextval('public."Place_id_seq"'::regclass);


--
-- Name: Promotion id; Type: DEFAULT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Promotion" ALTER COLUMN id SET DEFAULT nextval('public."Promotion_id_seq"'::regclass);


--
-- Name: Service id; Type: DEFAULT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Service" ALTER COLUMN id SET DEFAULT nextval('public."Service_id_seq"'::regclass);


--
-- Name: ServiceProvider id; Type: DEFAULT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."ServiceProvider" ALTER COLUMN id SET DEFAULT nextval('public."ServiceProvider_id_seq"'::regclass);


--
-- Name: Work id; Type: DEFAULT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Work" ALTER COLUMN id SET DEFAULT nextval('public."Work_id_seq"'::regclass);


--
-- Name: Appointment Appointment_pkey; Type: CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Appointment"
    ADD CONSTRAINT "Appointment_pkey" PRIMARY KEY (id);


--
-- Name: ClientAuth ClientAuth_email_key; Type: CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."ClientAuth"
    ADD CONSTRAINT "ClientAuth_email_key" UNIQUE (email);


--
-- Name: ClientAuth ClientAuth_pkey; Type: CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."ClientAuth"
    ADD CONSTRAINT "ClientAuth_pkey" PRIMARY KEY (client_id);


--
-- Name: Client Client_pkey; Type: CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Client"
    ADD CONSTRAINT "Client_pkey" PRIMARY KEY (id);


--
-- Name: JobType JobType_pkey; Type: CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."JobType"
    ADD CONSTRAINT "JobType_pkey" PRIMARY KEY (type);


--
-- Name: Notification Notification_pkey; Type: CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Notification"
    ADD CONSTRAINT "Notification_pkey" PRIMARY KEY (type);


--
-- Name: PlaceDoes PlaceDoes_pkey; Type: CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."PlaceDoes"
    ADD CONSTRAINT "PlaceDoes_pkey" PRIMARY KEY (place_id, jobtype_type);


--
-- Name: Place Place_pkey; Type: CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Place"
    ADD CONSTRAINT "Place_pkey" PRIMARY KEY (id);


--
-- Name: Promotion Promotion_pkey; Type: CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Promotion"
    ADD CONSTRAINT "Promotion_pkey" PRIMARY KEY (id);


--
-- Name: Provides Provides_check; Type: CHECK CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE public."Provides"
    ADD CONSTRAINT "Provides_check" CHECK ((public."checkProvides"(work_id, service_id) = true)) NOT VALID;


--
-- Name: Provides Provides_pkey; Type: CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Provides"
    ADD CONSTRAINT "Provides_pkey" PRIMARY KEY (work_id, service_id, day);


--
-- Name: ServiceInstance ServiceInstance_appointment_id_service_id_key; Type: CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."ServiceInstance"
    ADD CONSTRAINT "ServiceInstance_appointment_id_service_id_key" UNIQUE (appointment_id, service_id);


--
-- Name: ServiceInstance ServiceInstance_check; Type: CHECK CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE public."ServiceInstance"
    ADD CONSTRAINT "ServiceInstance_check" CHECK ((public."checkServiceInstance"(appointment_id, service_id, date) = true)) NOT VALID;


--
-- Name: ServiceInstance ServiceInstance_pkey; Type: CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."ServiceInstance"
    ADD CONSTRAINT "ServiceInstance_pkey" PRIMARY KEY (appointment_id, date);


--
-- Name: ServiceProviderAuth ServiceProviderAuth_email_key; Type: CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."ServiceProviderAuth"
    ADD CONSTRAINT "ServiceProviderAuth_email_key" UNIQUE (email);


--
-- Name: ServiceProviderAuth ServiceProviderAuth_pkey; Type: CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."ServiceProviderAuth"
    ADD CONSTRAINT "ServiceProviderAuth_pkey" PRIMARY KEY (serviceprovider_id);


--
-- Name: ServiceProvider ServiceProvider_pkey; Type: CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."ServiceProvider"
    ADD CONSTRAINT "ServiceProvider_pkey" PRIMARY KEY (id);


--
-- Name: Service Service_name_jobtype_type_key; Type: CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Service"
    ADD CONSTRAINT "Service_name_jobtype_type_key" UNIQUE (name, jobtype_type);


--
-- Name: Service Service_pkey; Type: CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Service"
    ADD CONSTRAINT "Service_pkey" PRIMARY KEY (id);


--
-- Name: Work Work_pkey; Type: CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Work"
    ADD CONSTRAINT "Work_pkey" PRIMARY KEY (id);


--
-- Name: Provides day_enum; Type: CHECK CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE public."Provides"
    ADD CONSTRAINT day_enum CHECK (((day >= 1) AND (day <= 7))) NOT VALID;


--
-- Name: Prefers prefers_pkey; Type: CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Prefers"
    ADD CONSTRAINT prefers_pkey PRIMARY KEY (client_id);


--
-- Name: promoApplied promoApplied_pkey; Type: CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."promoApplied"
    ADD CONSTRAINT "promoApplied_pkey" PRIMARY KEY (appointment_id, date, promotion_id);


--
-- Name: promoIncludes promoIncludes_check; Type: CHECK CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE public."promoIncludes"
    ADD CONSTRAINT "promoIncludes_check" CHECK ((public."checkPromo"(promotion_id, service_id) = true)) NOT VALID;


--
-- Name: promoIncludes promoIncludes_pkey; Type: CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."promoIncludes"
    ADD CONSTRAINT "promoIncludes_pkey" PRIMARY KEY (promotion_id, service_id);


--
-- Name: indexAppointmentWork; Type: INDEX; Schema: public; Owner: ezegi
--

CREATE INDEX "indexAppointmentWork" ON public."Appointment" USING hash (work_id);


--
-- Name: indexWorkServiceProvider; Type: INDEX; Schema: public; Owner: ezegi
--

CREATE INDEX "indexWorkServiceProvider" ON public."Work" USING hash (serviceprovider_id);


--
-- Name: Appointment Appointment_client_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Appointment"
    ADD CONSTRAINT "Appointment_client_id_fkey" FOREIGN KEY (client_id) REFERENCES public."Client"(id);


--
-- Name: Appointment Appointment_work_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Appointment"
    ADD CONSTRAINT "Appointment_work_id_fkey" FOREIGN KEY (work_id) REFERENCES public."Work"(id);


--
-- Name: ClientAuth ClientAuth_client_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."ClientAuth"
    ADD CONSTRAINT "ClientAuth_client_id_fkey" FOREIGN KEY (client_id) REFERENCES public."Client"(id) NOT VALID;


--
-- Name: PlaceDoes PlaceDoes_jobtype_type_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."PlaceDoes"
    ADD CONSTRAINT "PlaceDoes_jobtype_type_fkey" FOREIGN KEY (jobtype_type) REFERENCES public."JobType"(type) NOT VALID;


--
-- Name: PlaceDoes PlaceDoes_place_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."PlaceDoes"
    ADD CONSTRAINT "PlaceDoes_place_id_fkey" FOREIGN KEY (place_id) REFERENCES public."Place"(id) NOT VALID;


--
-- Name: Prefers Prefers_client_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Prefers"
    ADD CONSTRAINT "Prefers_client_id_fkey" FOREIGN KEY (client_id) REFERENCES public."Client"(id) NOT VALID;


--
-- Name: Prefers Prefers_notification_type_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Prefers"
    ADD CONSTRAINT "Prefers_notification_type_fkey" FOREIGN KEY (notification_type) REFERENCES public."Notification"(type) NOT VALID;


--
-- Name: Provides Provides_service_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Provides"
    ADD CONSTRAINT "Provides_service_id_fkey" FOREIGN KEY (service_id) REFERENCES public."Service"(id) NOT VALID;


--
-- Name: Provides Provides_work_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Provides"
    ADD CONSTRAINT "Provides_work_id_fkey" FOREIGN KEY (work_id) REFERENCES public."Work"(id) NOT VALID;


--
-- Name: ServiceInstance ServiceInstance_appointment_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."ServiceInstance"
    ADD CONSTRAINT "ServiceInstance_appointment_id_fkey" FOREIGN KEY (appointment_id) REFERENCES public."Appointment"(id);


--
-- Name: ServiceInstance ServiceInstance_service_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."ServiceInstance"
    ADD CONSTRAINT "ServiceInstance_service_id_fkey" FOREIGN KEY (service_id) REFERENCES public."Service"(id);


--
-- Name: ServiceProviderAuth ServiceProviderAuth_serviceprovider_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."ServiceProviderAuth"
    ADD CONSTRAINT "ServiceProviderAuth_serviceprovider_id_fkey" FOREIGN KEY (serviceprovider_id) REFERENCES public."ServiceProvider"(id) NOT VALID;


--
-- Name: Service Service_jobtype_type_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Service"
    ADD CONSTRAINT "Service_jobtype_type_fkey" FOREIGN KEY (jobtype_type) REFERENCES public."JobType"(type) NOT VALID;


--
-- Name: Work Work_place_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Work"
    ADD CONSTRAINT "Work_place_id_fkey" FOREIGN KEY (place_id) REFERENCES public."Place"(id) NOT VALID;


--
-- Name: Work Work_serviceprovider_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."Work"
    ADD CONSTRAINT "Work_serviceprovider_id_fkey" FOREIGN KEY (serviceprovider_id) REFERENCES public."ServiceProvider"(id) NOT VALID;


--
-- Name: promoApplied promoApplied_appointment_id_date_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."promoApplied"
    ADD CONSTRAINT "promoApplied_appointment_id_date_fkey" FOREIGN KEY (appointment_id, date) REFERENCES public."ServiceInstance"(appointment_id, date);


--
-- Name: promoApplied promoApplied_promotion_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."promoApplied"
    ADD CONSTRAINT "promoApplied_promotion_id_fkey" FOREIGN KEY (promotion_id) REFERENCES public."Promotion"(id);


--
-- Name: promoIncludes promoIncludes_promotion_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."promoIncludes"
    ADD CONSTRAINT "promoIncludes_promotion_id_fkey" FOREIGN KEY (promotion_id) REFERENCES public."Promotion"(id);


--
-- Name: promoIncludes promoIncludes_service_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ezegi
--

ALTER TABLE ONLY public."promoIncludes"
    ADD CONSTRAINT "promoIncludes_service_id_fkey" FOREIGN KEY (service_id) REFERENCES public."Service"(id);


--
-- PostgreSQL database dump complete
--

