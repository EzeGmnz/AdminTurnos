from rest_framework import serializers
from restauth.models import CustomUser


class CustomUserSerializer(serializers.ModelSerializer):

    class Meta:
        model = CustomUser
        fields = ['id', 'email', 'given_name', 'family_name']
