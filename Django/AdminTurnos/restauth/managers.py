from django.contrib import auth
from django.contrib.auth.models import (
    BaseUserManager
)


class CustomUserManager(BaseUserManager):

    def create_user(self, email, isprovider):
        user = self.model(
            email=self.normalize_email(email),
        )

        user.isprovider = isprovider
        user.isclient = not isprovider
        user.isproviderpro = False

        user.save(using=self._db)
        return user

    def get_or_create(self, email, isprovider):
        user_model = auth.get_user_model()
        try:
            return user_model.objects.get(email=email), False
        except user_model.DoesNotExist:
            return self.create_user(email=email, isprovider=isprovider), True
