from rest_framework import serializers

from .models import Place, JobRequest, Service, Job, Appointment, Serviceinstance


class JobRequestSerializer(serializers.Serializer):
    place = serializers.PrimaryKeyRelatedField(read_only=True)
    serviceprovider_from = serializers.PrimaryKeyRelatedField(read_only=True)

    def update(self, instance, validated_data):
        instance.place = validated_data.get('place', instance.place)
        instance.serviceprovider_from = validated_data.get('serviceprovider_from',
                                                           instance.serviceprovider_from)

        return instance

    def create(self, validated_data):
        return JobRequest.objects.create(**validated_data)


class ServiceSerializer(serializers.Serializer):
    id = serializers.IntegerField()
    name = serializers.CharField(max_length=30)
    jobtype = serializers.PrimaryKeyRelatedField(read_only=True)

    def update(self, instance, validated_data):
        instance.name = validated_data.get('name', instance.name)
        instance.jobtype = validated_data.get('jobtype', instance.jobtype)

        return instance

    def create(self, validated_data):
        return Service.objects.create(**validated_data)


class PlaceSerializer(serializers.ModelSerializer):
    class Meta:
        model = Place
        fields = ['id', 'street', 'streetnumber', 'businessname']


class JobSerializer(serializers.ModelSerializer):
    id = serializers.IntegerField()
    place = PlaceSerializer()

    class Meta:
        model = Job
        fields = ['id', 'place']


class AppointmentSerializer(serializers.Serializer):
    id = serializers.IntegerField()
    job = serializers.PrimaryKeyRelatedField(read_only=True)
    client = serializers.PrimaryKeyRelatedField(read_only=True)
    date = serializers.DateField()
    description = serializers.CharField(max_length=30)

    def update(self, instance, validated_data):
        instance.job = validated_data.get('instance.job', instance.job)
        instance.client = validated_data.get('instance.client', instance.client)
        instance.date = validated_data.get('instance.date', instance.date)
        instance.description = validated_data.get('instance.description', instance.description)

        return instance

    def create(self, validated_data):
        return Appointment.objects.create(**validated_data)


class ServiceInstanceSerializer(serializers.Serializer):
    appointment = serializers.PrimaryKeyRelatedField(read_only=True)
    date = serializers.DateTimeField()
    service = serializers.PrimaryKeyRelatedField(read_only=True)

    def update(self, instance, validated_data):
        instance.appointment = validated_data.get('appointment', instance.appointment)
        instance.date = validated_data.get('date', instance.date)
        instance.service = validated_data.get('service', instance.service)

    def create(self, validated_data):
        return Serviceinstance.objects.create(**validated_data)
