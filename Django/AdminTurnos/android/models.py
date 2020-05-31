from django.contrib import auth
from django.contrib.postgres.fields import ArrayField
from django.core.validators import MaxValueValidator, MinValueValidator
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

    class Meta:
        db_table = 'Job'
        unique_together = (('serviceprovider', 'place'),)


class Placedoes(models.Model):
    place = models.OneToOneField(Place, models.CASCADE, primary_key=True)
    jobtype = models.ForeignKey(Jobtype, models.CASCADE, db_column='jobtype')

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
    day_of_week = models.IntegerField(validators=[MaxValueValidator(7), MinValueValidator(1)])
    day_start = models.TimeField()
    day_end = models.TimeField()
    pause_start = models.TimeField(blank=True, null=True)
    pause_end = models.TimeField(blank=True, null=True)

    def clean(self):
        job_schedules = DaySchedule.objects.filter(job=self.job)
        for s in job_schedules:
            if s.day_of_week == self.day_of_week:
                raise ValidationError("Job already has a schedule for this day of week")

    def service_provided_in(self, service):
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

    def clean(self):
        if self.day_schedule.job != self.job:
            raise ValidationError('Day schedule does not belong to job')

        job_types = Placedoes.objects.filter(job=self.job).values('jobtype')
        services = []
        for j in job_types:
            services.extend(Service.objects.filter(jobtype=j))
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
    description = models.TextField(blank=True, null=True)

    class Meta:
        db_table = 'Appointment'


class Serviceinstance(models.Model):
    appointment = models.OneToOneField(Appointment, models.CASCADE, primary_key=True)
    date = models.DateTimeField()
    service = models.ForeignKey(Service, models.CASCADE)

    def clean(self):

        weekday = self.date.weekday()
        day_schedules = Provides.objects.filter(job=self.appointment.job)
        for d in day_schedules:
            if d.day_of_week == weekday:
                if not d.service_provided_in(self.service):
                    raise ValidationError('Service is not provided in given day')

        appointments_in_day = Appointment.objects.filter(job=self.appointment.job, date=self.date.date())
        duration, max_parallelism = self.get_duration_parallelism()
        curr_parallelism = 0
        for a in appointments_in_day:
            service_at_same_time = Serviceinstance.objects.get(
                appointment=a,
                service=self.service,
                date__range=[self.date, self.date + duration])

            if service_at_same_time.exists():
                curr_parallelism += 1

        if curr_parallelism >= max_parallelism:
            raise ValidationError('Max parallelism for service reached')

    def get_duration_parallelism(self):
        # getting duration for that day
        provides = Provides.objects.get(job=self.appointment.job, service=self.service)
        for d in provides:
            if provides.day_schedule.day_of_week == self.date.weekday():
                return d.duration, d.parallelism

    class Meta:
        db_table = 'ServiceInstance'
        unique_together = (('appointment', 'service'), ('appointment', 'date'),)


class Promotion(models.Model):
    id = models.BigAutoField(primary_key=True)
    job = models.ForeignKey(Job, models.CASCADE)
    since = models.DateField()
    to = models.DateField()
    description = models.TextField(blank=True, null=True)
    day_schedules = ArrayField(models.IntegerField())

    class Meta:
        db_table = 'Promotion'


class Promoincludes(models.Model):
    promotion = models.OneToOneField(Promotion, models.CASCADE, primary_key=True)
    service = models.ForeignKey(Service, models.CASCADE)
    discount = models.FloatField(blank=True, null=True)

    def clean(self):
        schedule_ids = self.promotion.day_schedules

        for x in schedule_ids:
            schedule = DaySchedule.objects.get(id=x)
            if not schedule.service_provided_in(self.service):
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
    place = models.OneToOneField(Place, models.CASCADE, primary_key=True)
    serviceprovider_from = models.ForeignKey(auth.get_user_model(), models.CASCADE)

    class Meta:
        db_table = 'JobRequest'
        unique_together = (('place', 'serviceprovider_from'),)
