from django.contrib import auth
from django.contrib.postgres.fields import ArrayField
from django.db import models
from rest_framework.exceptions import ValidationError


class Jobtype(models.Model):
    type = models.CharField(primary_key=True, max_length=30)

    class Meta:
        db_table = 'JobType'


class Notification(models.Model):
    type = models.CharField(primary_key=True, max_length=30)

    class Meta:
        db_table = 'Notification'


class Place(models.Model):
    id = models.BigAutoField(primary_key=True)
    serviceprovider = models.ForeignKey(auth.get_user_model(), on_delete=models.CASCADE)
    street = models.CharField(max_length=30)
    streetnumber = models.CharField(max_length=30)
    apnumber = models.CharField(max_length=30)
    city = models.CharField(max_length=30)
    state = models.CharField(max_length=30)
    country = models.CharField(max_length=30)
    businessname = models.CharField(max_length=30)
    phonenumber = models.CharField(max_length=30, blank=True, null=True)
    email = models.CharField(max_length=30)

    class Meta:
        db_table = 'Place'


class Job(models.Model):
    id = models.BigAutoField(primary_key=True)
    serviceprovider = models.ForeignKey(auth.get_user_model(), models.CASCADE)
    place = models.ForeignKey(Place, models.CASCADE)

    def clean(self):
        places = Job.objects.filter(serviceprovider=self.serviceprovider, place=self.place)
        if len(places) > 0:
            raise ValidationError("Job for this service provider already exists in this place")

    def get_current_parallelism(self, service, timestamp):
        appointments_in_day = Appointment.objects.filter(job=self, date=timestamp.date())

        curr_parallelism = 0
        for a in appointments_in_day:
            service_at_same_time = Serviceinstance.objects.filter(
                appointment=a,
                service=service,
                timestamp=timestamp)
            if service_at_same_time.exists():
                curr_parallelism += 1

        return curr_parallelism

    def get_duration(self, service, date):
        provides = Provides.objects.get(job=self,
                                        service=service,
                                        day_schedule__day_of_week=date.weekday())
        return provides.duration

    def is_at_max_parallelism(self, service, timestamp):
        provides = Provides.objects.get(
            job=self,
            service=service,
            day_schedule__day_of_week=timestamp.date().weekday())

        max_parallelism = provides.parallelism
        return self.get_current_parallelism(service, timestamp) >= max_parallelism

    class Meta:
        db_table = 'Job'
        unique_together = (('serviceprovider', 'place'),)


class Placedoes(models.Model):
    id = models.BigAutoField(primary_key=True)
    place = models.ForeignKey(Place, models.CASCADE)
    jobtype = models.ForeignKey(Jobtype, models.CASCADE)

    class Meta:
        db_table = 'PlaceDoes'
        unique_together = (('place', 'jobtype'),)


class Prefers(models.Model):
    client = models.OneToOneField(auth.get_user_model(), models.CASCADE, primary_key=True)
    notification_type = models.ForeignKey(Notification, models.CASCADE, db_column='notification_type')
    contact_info = models.CharField(max_length=30)

    class Meta:
        db_table = 'Prefers'


class Service(models.Model):
    id = models.BigAutoField(primary_key=True)
    name = models.CharField(max_length=30)
    jobtype = models.ForeignKey(Jobtype, models.CASCADE, db_column='jobtype')

    class Meta:
        db_table = 'Service'
        unique_together = (('name', 'jobtype'),)


class DaySchedule(models.Model):
    id = models.BigAutoField(primary_key=True)
    job = models.ForeignKey(Job, models.CASCADE)
    day_of_week = models.IntegerField()
    day_start = models.TimeField()
    day_end = models.TimeField()
    pause_start = models.TimeField(blank=True, null=True)
    pause_end = models.TimeField(blank=True, null=True)
    is_active = models.BooleanField(default=True)

    def save(self, *args, **kwargs):
        self.full_clean()
        return super(DaySchedule, self).save(*args, **kwargs)

    def clean(self):
        if self.day_of_week > 7 or self.day_of_week < 1:
            raise ValidationError('Day of week must be an integer between 1 and 7')

    def is_service_in_schedule(self, service):
        provides_list = Provides.objects.filter(day_schedule=self)
        for p in provides_list:
            if p.service == service:
                return True
        return False

    class Meta:
        db_table = 'DaySchedule'
        unique_together = (('job', 'day_of_week'),)


class Provides(models.Model):
    id = models.BigAutoField(primary_key=True)
    job = models.ForeignKey(Job, models.CASCADE)
    day_schedule = models.ForeignKey(DaySchedule, models.CASCADE)
    service = models.ForeignKey(Service, models.CASCADE)
    cost = models.FloatField()
    duration = models.TimeField()
    parallelism = models.IntegerField()

    def save(self, *args, **kwargs):
        self.full_clean()
        return super(Provides, self).save(*args, **kwargs)

    def clean(self):
        if self.day_schedule.job != self.job:
            raise ValidationError('Day schedule does not belong to job')

        job_types = Placedoes.objects.filter(place=self.job.place)
        services = []
        for j in job_types:
            services.extend(Service.objects.filter(jobtype=j.jobtype))

        if self.service not in services:
            raise ValidationError('Service is not provided in job\'s place')

    class Meta:
        db_table = 'Provides'
        unique_together = (('day_schedule', 'service'),)


class Appointment(models.Model):
    id = models.BigAutoField(primary_key=True)
    job = models.ForeignKey(Job, models.CASCADE)
    client = models.ForeignKey(auth.get_user_model(), models.CASCADE)
    date = models.DateField()

    class Meta:
        db_table = 'Appointment'


class Serviceinstance(models.Model):
    id = models.BigAutoField(primary_key=True, default=None)
    appointment = models.ForeignKey(Appointment, models.CASCADE)
    timestamp = models.DateTimeField()
    service = models.ForeignKey(Service, models.CASCADE)

    def save(self, *args, **kwargs):
        self.full_clean()
        return super(Serviceinstance, self).save(*args, **kwargs)

    def clean(self):
        if not self.is_provided():
            raise ValidationError('Service is not provided in given day')
        if self.is_at_max_parallelism():
            raise ValidationError('Service currently at maximum parallelism')
        if not self.date_equals_appointment_one():
            raise ValidationError('Date doesnt match appointment\'s date')

    def date_equals_appointment_one(self):
        appointment_date = self.appointment.date
        if self.timestamp.date() != appointment_date:
            return False
        return True

    def is_at_max_parallelism(self):
        if self.appointment.job.is_at_max_parallelism(self.service, self.timestamp):
            return True
        return False

    def is_provided(self):
        weekday = self.timestamp.weekday()
        day_schedules = DaySchedule.objects.filter(job=self.appointment.job)
        for schedule in day_schedules:
            if schedule.day_of_week == weekday:
                if not schedule.is_service_in_schedule(self.service):
                    return False
        return True

    class Meta:
        db_table = 'ServiceInstance'
        unique_together = (('appointment', 'service'), ('appointment', 'timestamp'),)


class Promotion(models.Model):
    id = models.BigAutoField(primary_key=True)
    job = models.ForeignKey(Job, models.CASCADE)
    since = models.DateField()
    to = models.DateField()
    description = models.TextField(blank=True, null=True)
    weekday_list = ArrayField(models.IntegerField())

    class Meta:
        db_table = 'Promotion'


class Promoincludes(models.Model):
    promotion = models.OneToOneField(Promotion, models.CASCADE, primary_key=True)
    service = models.ForeignKey(Service, models.CASCADE)
    discount = models.FloatField(blank=True, null=True)

    def save(self, *args, **kwargs):
        self.full_clean()
        return super(Promoincludes, self).save(*args, **kwargs)

    def clean(self):
        schedule_ids = self.promotion.day_schedules

        for x in schedule_ids:
            schedule = DaySchedule.objects.get(job=self.promotion.job, day_of_week=x)
            if not schedule.is_service_in_schedule(self.service):
                raise ValidationError('Service is not provided in promotion days')

    class Meta:
        db_table = 'promoIncludes'
        unique_together = (('promotion', 'service'),)


class Promoapplied(models.Model):
    promotion = models.ForeignKey(Promoincludes, models.CASCADE)
    appointment = models.ForeignKey(Serviceinstance, models.CASCADE)
    service_id = models.BigIntegerField(primary_key=True)

    class Meta:
        db_table = 'promoApplied'
        unique_together = (('service_id', 'appointment', 'promotion'),)


class JobRequest(models.Model):
    id = models.BigAutoField(primary_key=True)
    place = models.ForeignKey(Place, models.CASCADE)
    serviceprovider = models.ForeignKey(auth.get_user_model(), models.CASCADE)

    class Meta:
        db_table = 'JobRequest'
        unique_together = (('place', 'serviceprovider'),)


class ScheduleTemplate(models.Model):
    id = models.BigAutoField(primary_key=True)
    name = models.CharField(max_length=30)
    open = models.TimeField()
    close = models.TimeField()
    days = ArrayField(models.IntegerField())

    class Meta:
        db_table = 'ScheduleTemplate'
