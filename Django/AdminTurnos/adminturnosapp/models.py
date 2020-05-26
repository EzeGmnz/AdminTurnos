from django.db import models
from django.contrib.auth.models import AbstractBaseUser
from .managers import CustomUserManager

class CustomUser(AbstractBaseUser):
	email = models.EmailField(unique=True)
	isProvider = models.BooleanField(default=False)
	isClient = models.BooleanField(default=False)
	isProviderPro = models.BooleanField(default=False)

	USERNAME_FIELD = 'email'
	REQUIRED_FIELDS = []

	objects = CustomUserManager()

	def __str__(self):
		return self.email

class Test(models.Model):
	first_name = models.CharField(max_length=30)

	class Meta:
		permissions = (
			('can_access', 'Can Access'),
		)
