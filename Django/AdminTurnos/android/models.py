from django.contrib import auth
from django.contrib.postgres.fields import ArrayField
from django.db import models


class Appointment(models.Model):
    id = models.BigAutoField(primary_key=True)
    job = models.ForeignKey('Job', models.CASCADE)
    client = models.ForeignKey(auth.get_user_model(), models.CASCADE)
    date = models.DateField()
    description = models.TextField(blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'Appointment'


class Job(models.Model):
    id = models.BigAutoField(primary_key=True)
    serviceprovider = models.ForeignKey(auth.get_user_model(), models.CASCADE)
    place = models.ForeignKey('Place', models.CASCADE)

    class Meta:
        managed = False
        db_table = 'Job'


class Jobtype(models.Model):
    type = models.CharField(primary_key=True, max_length=30)

    class Meta:
        managed = False
        db_table = 'JobType'


class Notification(models.Model):
    type = models.CharField(primary_key=True, max_length=30)

    class Meta:
        managed = False
        db_table = 'Notification'


class Place(models.Model):
    id = models.BigAutoField(primary_key=True)
    serviceprovider_owner_id = models.BigIntegerField()
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
        managed = False
        db_table = 'Place'


class Placedoes(models.Model):
    place = models.OneToOneField(Place, models.CASCADE, primary_key=True)
    jobtype_type = models.ForeignKey(Jobtype, models.CASCADE, db_column='jobtype_type')

    class Meta:
        managed = True
        db_table = 'PlaceDoes'
        unique_together = (('place', 'jobtype_type'),)


class Prefers(models.Model):
    client = models.OneToOneField(auth.get_user_model(), models.CASCADE, primary_key=True)
    notification_type = models.ForeignKey(Notification, models.CASCADE, db_column='notification_type')
    contact_info = models.CharField(max_length=30)

    class Meta:
        managed = False
        db_table = 'Prefers'


class Promotion(models.Model):
    id = models.BigAutoField(primary_key=True)
    job = models.ForeignKey(Job, models.CASCADE)
    since = models.DateField()
    to = models.DateField()
    description = models.TextField(blank=True, null=True)
    days = ArrayField(models.IntegerField())

    class Meta:
        managed = False
        db_table = 'Promotion'


class Provides(models.Model):
    job = models.OneToOneField(Job, models.CASCADE, primary_key=True)
    service = models.ForeignKey('Service', models.CASCADE)
    day = models.IntegerField()
    cost = models.FloatField()
    duration = models.TimeField()
    day_start = models.TimeField()
    day_end = models.TimeField()
    pause_start = models.TimeField(blank=True, null=True)
    pause_end = models.TimeField(blank=True, null=True)
    parallelism = models.IntegerField()

    class Meta:
        managed = False
        db_table = 'Provides'
        unique_together = (('job', 'service', 'day'),)


class Service(models.Model):
    id = models.BigAutoField(primary_key=True)
    name = models.CharField(max_length=30)
    jobtype_type = models.ForeignKey(Jobtype, models.CASCADE, db_column='jobtype_type')

    class Meta:
        managed = False
        db_table = 'Service'
        unique_together = (('name', 'jobtype_type'),)


class Serviceinstance(models.Model):
    appointment = models.OneToOneField(Appointment, models.CASCADE, primary_key=True)
    date = models.DateTimeField()
    service = models.ForeignKey(Service, models.CASCADE)

    class Meta:
        managed = False
        db_table = 'ServiceInstance'
        unique_together = (('appointment', 'service'), ('appointment', 'date'),)


class Promoapplied(models.Model):
    promotion = models.ForeignKey('Promoincludes', models.CASCADE)
    appointment = models.ForeignKey(Serviceinstance, models.CASCADE)
    service_id = models.BigIntegerField(primary_key=True)

    class Meta:
        managed = False
        db_table = 'promoApplied'
        unique_together = (('service_id', 'appointment', 'promotion'),)


class Promoincludes(models.Model):
    promotion = models.OneToOneField(Promotion, models.CASCADE, primary_key=True)
    service = models.ForeignKey(Service, models.CASCADE)
    discount = models.FloatField(blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'promoIncludes'
        unique_together = (('promotion', 'service'),)


class JobRequest(models.Model):
    place = models.OneToOneField(Place, models.CASCADE, primary_key=True)
    serviceprovider_from = models.ForeignKey(auth.get_user_model(), models.CASCADE)

    class Meta:
        managed = True
        db_table = 'JobRequest'
        unique_together = (('place', 'serviceprovider_from'),)
