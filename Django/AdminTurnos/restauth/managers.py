from django.db import models
from django.contrib import auth
from django.contrib.auth.models import (
    BaseUserManager, AbstractBaseUser
)

class CustomUserManager(BaseUserManager):

    def create_user(self, email, isProvider):
        user = self.model(
            email=self.normalize_email(email),
        )

        user.isProvider = isProvider
        user.isClient = not isProvider
        user.isProviderPro = False

        user.save(using=self._db)
        return user

    def get_or_create(self, email, isProvider):
        UserModel = auth.get_user_model()
        try:
            return (UserModel.objects.get(email=email), False)
        except UserModel.DoesNotExist:
            return (self.create_user(email=email,isProvider=isProvider), True)

