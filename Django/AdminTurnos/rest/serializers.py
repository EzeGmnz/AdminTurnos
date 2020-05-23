from rest_framework import serializers
from rest.models import ServiceProvider

class ServiceProviderSerializer(serializers.ModelSerializer):
    class Meta:
        model = ServiceProvider
        fields = ['id', 'name', 'is_pro']