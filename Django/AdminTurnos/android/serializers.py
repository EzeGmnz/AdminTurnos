from rest_framework import serializers

from restauth.serializers import CustomUserSerializer
from .models import Place, JobRequest, Service, Job, Appointment, Serviceinstance, DaySchedule, Provides


class ServiceSerializer(serializers.ModelSerializer):
    class Meta:
        model = Service
        fields = ['id', 'name', 'jobtype']


class PlaceSerializer(serializers.ModelSerializer):
    serviceprovider = CustomUserSerializer()

    class Meta:
        model = Place
        fields = ['id', 'street', 'streetnumber', 'businessname', 'serviceprovider']


class JobRequestSerializer(serializers.ModelSerializer):
    place = PlaceSerializer()
    serviceprovider_from = CustomUserSerializer()

    class Meta:
        model = JobRequest
        fields = ['place', 'serviceprovider']


class JobSerializer(serializers.ModelSerializer):
    id = serializers.IntegerField()
    place = PlaceSerializer()

    class Meta:
        model = Job
        fields = ['id', 'place']


class AppointmentSerializer(serializers.ModelSerializer):
    client = CustomUserSerializer()

    class Meta:
        model = Appointment
        fields = ['job', 'client', 'date']


class ServiceInstanceSerializer(serializers.ModelSerializer):
    service = ServiceSerializer()

    class Meta:
        model = Serviceinstance
        fields = ['id', 'timestamp', 'service']


class DayScheduleSerializer(serializers.ModelSerializer):
    class Meta:
        model = DaySchedule
        fields = ['id', 'day_of_week', 'day_start', 'day_end', 'pause_start', 'pause_end']


class ProvidesSerializer(serializers.ModelSerializer):
    service = ServiceSerializer()

    class Meta:
        model = Provides
        fields = ['id', 'service', 'cost', 'duration', 'parallelism']
