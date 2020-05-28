from django.http import JsonResponse
from django.utils.decorators import method_decorator
from django.views.decorators.csrf import csrf_exempt
from rest_framework.permissions import IsAuthenticated
from rest_framework.views import APIView

from .models import Place, Placedoes, JobRequest
from .permissions import IsOwner, IsProvider
from .serializers import PlaceSerializer, JobRequestSerializer


# custom api view implementing get_object with permission check
class CustomAPIView(APIView):
    responseOK = {'result': 'OK'}

    def get_object(self, request, obj_model, pk):
        obj = obj_model.objects.get(pk=pk)
        self.check_object_permissions(request, obj)
        return obj

    def returnOK(self):
        return JsonResponse(self.responseOK)


@method_decorator(csrf_exempt, name='dispatch')
class UserProfile(APIView):
    permission_classes = [IsAuthenticated & IsProvider]

    def get(self, request):
        return JsonResponse({
            'isProvider': request.user.isProvider,
            'isClient': request.user.isClient,
        })


@method_decorator(csrf_exempt, name='dispatch')
class NewPlace(CustomAPIView):
    permission_classes = [IsAuthenticated & IsProvider]

    # street, streetnumber, apnumber, city, state, country, businessname, phonenumber, email
    def post(self, request):
        params = list(request.POST.values())
        params.insert(0, None)  # Autoincrement field
        params.insert(1, request.user.id)

        place = Place(*params)
        place.save()
        return JsonResponse(PlaceSerializer(place).data)


@method_decorator(csrf_exempt, name='dispatch')
class DropPlace(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner]

    # place_id
    def post(self, request):
        place = self.get_object(request, Place, request.POST.get('place_id'))
        place.delete()

        return self.returnOK()


@method_decorator(csrf_exempt, name='dispatch')
class PlaceDoes(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner]

    # place_id, jobtype_type
    def post(self, request):
        place = self.get_object(request, Place, request.POST.get('place_id'))
        place_does = Placedoes(place.id, request.POST.get('jobtype_type'))
        place_does.save()

        return self.returnOK()


@method_decorator(csrf_exempt, name='dispatch')
class PlacesOwned(CustomAPIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        places = Place.objects.filter(serviceprovider_owner_id=request.user.id)
        output = []
        for p in places:
            output.append(PlaceSerializer(p).data)
        return JsonResponse({'places': output})


@method_decorator(csrf_exempt, name='dispatch')
class SearchPlace(CustomAPIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        businessname = request.data.get('businessname')
        output = []
        if businessname is not None and businessname.strip() != '':
            places = Place.objects.filter(businessname__contains=businessname)
            for p in places:
                output.append(PlaceSerializer(p).data)
        return JsonResponse({'places': output})


@method_decorator(csrf_exempt, name='dispatch')
class ViewJobRequests(CustomAPIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        places = Place.objects.filter(serviceprovider_owner_id=request.user.id)
        output = {}
        for p in places:
            job_requests = JobRequest.objects.filter(place=p.id)
            jb_list = []
            for jr in job_requests:
                jb_list.append(JobRequestSerializer(jr).data)
            output[p.id] = jb_list

        return JsonResponse(output)


@method_decorator(csrf_exempt, name='dispatch')
class NewJobRequest(CustomAPIView):
    permission_classes = [IsAuthenticated]

    def post(self, request):
        place_id = request.body.get('place_id')
        job_request = JobRequest(place_id, request.user.id)
        job_request.save()
        return self.returnOK()
