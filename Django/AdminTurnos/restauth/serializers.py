from rest_framework import serializers
from restauth.models import CustomUser


class CustomUserSerializer(serializers.ModelSerializer):
    email = serializers.CharField(max_length=30)

    class Meta:
        model = CustomUser
        fields = ['email']
