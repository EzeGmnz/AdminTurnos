from django.db import models

# Create your models here.

# Service Provider Representation
# This is an auto-generated Django model module.
# You'll have to do the following manually to clean this up:
#   * Rearrange models' order
#   * Make sure each model has one field with primary_key=True
#   * Make sure each ForeignKey and OneToOneField has `on_delete` set to the desired behavior
#   * Remove `managed = False` lines if you wish to allow Django to create, modify, and delete the table
# Feel free to rename the models, but don't rename db_table values or field names.
from django.db import models


class Appointment(models.Model):
    id = models.BigAutoField(primary_key=True)
    job = models.ForeignKey('Job', models.DO_NOTHING)
    client = models.ForeignKey('Client', models.DO_NOTHING)
    date = models.DateField()
    description = models.TextField(blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'Appointment'


class Client(models.Model):
    id = models.BigAutoField(primary_key=True)
    name = models.CharField(max_length=30)

    class Meta:
        managed = False
        db_table = 'Client'


class ClientAuth(models.Model):
    client = models.OneToOneField(Client, models.DO_NOTHING, primary_key=True)
    email = models.CharField(unique=True, max_length=30)

    class Meta:
        managed = False
        db_table = 'ClientAuth'


class Job(models.Model):
    id = models.BigAutoField(primary_key=True)
    serviceprovider = models.ForeignKey('ServiceProvider', models.DO_NOTHING)
    place = models.ForeignKey('Place', models.DO_NOTHING)

    class Meta:
        managed = False
        db_table = 'Job'


class JobType(models.Model):
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
    streetnumber = models.IntegerField()
    apnumber = models.IntegerField()
    city = models.CharField(max_length=30)
    state = models.CharField(max_length=30)
    country = models.CharField(max_length=30)
    businessname = models.CharField(max_length=30)
    phonenumber = models.CharField(max_length=30, blank=True, null=True)
    email = models.CharField(max_length=30)

    class Meta:
        managed = False
        db_table = 'Place'


class PlaceDoes(models.Model):
    place = models.OneToOneField(Place, models.DO_NOTHING, primary_key=True)
    jobtype_type = models.ForeignKey(JobType, models.DO_NOTHING, db_column='jobtype_type')

    class Meta:
        managed = False
        db_table = 'PlaceDoes'
        unique_together = (('place', 'jobtype_type'),)


class Prefers(models.Model):
    client = models.OneToOneField(Client, models.DO_NOTHING, primary_key=True)
    notification_type = models.ForeignKey(Notification, models.DO_NOTHING, db_column='notification_type')
    contact_info = models.CharField(max_length=30)

    class Meta:
        managed = False
        db_table = 'Prefers'


class Promotion(models.Model):
    id = models.BigAutoField(primary_key=True)
    job = models.ForeignKey(Job, models.DO_NOTHING)
    since = models.DateField()
    to = models.DateField()
    description = models.TextField(blank=True, null=True)
    days = models.TextField(blank=True, null=True)  # This field type is a guess.

    class Meta:
        managed = False
        db_table = 'Promotion'


class Provides(models.Model):
    job = models.OneToOneField(Job, models.DO_NOTHING, primary_key=True)
    service = models.ForeignKey('Service', models.DO_NOTHING)
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
    jobtype_type = models.ForeignKey(JobType, models.DO_NOTHING, db_column='jobtype_type')

    class Meta:
        managed = False
        db_table = 'Service'
        unique_together = (('name', 'jobtype_type'),)


class ServiceInstance(models.Model):
    appointment = models.OneToOneField(Appointment, models.DO_NOTHING, primary_key=True)
    date = models.DateTimeField()
    service = models.ForeignKey(Service, models.DO_NOTHING)

    class Meta:
        managed = False
        db_table = 'ServiceInstance'
        unique_together = (('appointment', 'date'), ('appointment', 'service'),)


class ServiceProvider(models.Model):
    id = models.BigAutoField(primary_key=True)
    name = models.CharField(max_length=30)
    is_pro = models.BooleanField()

    class Meta:
        managed = False
        db_table = 'ServiceProvider'


class ServiceProviderAuth(models.Model):
    serviceprovider = models.OneToOneField(ServiceProvider, models.DO_NOTHING, primary_key=True)
    email = models.CharField(unique=True, max_length=30)

    class Meta:
        managed = False
        db_table = 'ServiceProviderAuth'


class PromoApplied(models.Model):
    promotion = models.ForeignKey('PromoIncludes', models.DO_NOTHING)
    appointment = models.ForeignKey(ServiceInstance, models.DO_NOTHING)
    service_id = models.BigIntegerField(primary_key=True)

    class Meta:
        managed = False
        db_table = 'PromoApplied'
        unique_together = (('service_id', 'appointment', 'promotion'),)


class PromoIncludes(models.Model):
    promotion = models.OneToOneField(Promotion, models.DO_NOTHING, primary_key=True)
    service = models.ForeignKey(Service, models.DO_NOTHING)
    discount = models.FloatField(blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'PromoIncludes'
        unique_together = (('promotion', 'service'),)