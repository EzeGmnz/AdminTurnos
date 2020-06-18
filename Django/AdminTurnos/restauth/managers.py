from django.contrib import auth
from django.contrib.auth.models import (
    BaseUserManager
)


class CustomUserManager(BaseUserManager):

    def create_user(self, email, isprovider, idinfo):
        user = self.model(
            email=self.normalize_email(email),
        )

        user.isprovider = isprovider
        user.isclient = not isprovider
        user.isproviderpro = False
        user.given_name = idinfo['given_name']
        user.family_name = idinfo['family_name']

        user.save(using=self._db)
        return user

    def get_or_create(self, email, isprovider, idinfo):
        user_model = auth.get_user_model()
        try:
            user = user_model.objects.get(email=email)
            if not user.isprovider and isprovider:
                user.isprovider = True
            elif not user.isclient and not isprovider:
                user.isclient = True
            user.save()
            return user, False
        except user_model.DoesNotExist:
            return self.create_user(email=email, isprovider=isprovider, idinfo=idinfo), True
