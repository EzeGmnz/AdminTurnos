from .models import CustomUser
from django import forms

# Register your models here.
class ServiceProviderCreationForm(forms.ModelForm):

	class Meta:
		model = CustomUser
		fields = ['email', 'isProvider', 'isClient', 'isProviderPro']
