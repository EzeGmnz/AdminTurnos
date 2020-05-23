from django.http import HttpResponse, JsonResponse
from django.views.decorators.csrf import csrf_exempt
from rest_framework.parsers import JSONParser
from rest.models import ServiceProvider
from rest.serializers import ServiceProviderSerializer

# Create your views here.
@csrf_exempt
def serviceprovider_list(request):

    if request.method == 'GET':
        ServiceProviders = ServiceProvider.objects.all()
        serializer = ServiceProviderSerializer(ServiceProviders, many=True)
        return JsonResponse(serializer.data, safe=False)

    elif request.method == 'POST':
        data = JSONParser().parse(request)
        serializer = ServiceProviderSerializer(data=data)
        if serializer.is_valid():
            serializer.save()
            return JsonResponse(serializer.data, status=201)
        return JsonResponse(serializer.errors, status=400)

# from django.db import connection

# with connection.cursor() as cursor:
#     cursor.execute("SQL STATEMENT CAN BE ANYTHING")
#     data = cursor.fetchone()

@csrf_exempt
def serviceprovider_detail(request, pk):
    try:
        serviceProvider = ServiceProvider.objects.get(pk=pk)
    except ServiceProvider.DoesNotExist:
        return HttpResponse(status=404)

    if request.method== 'GET':
        serializer = ServiceProviderSerializer(serviceProvider)
        return JsonResponse(serializer.data)

    elif request.method == 'PUT':
        data = JSONParser().parse(request)
        serializer = ServiceProviderSerializer(serviceProvider, data=data)
        if serializer.is_valid():
            serializer.save()
            return JsonResponse(serializer.data)
        return JsonResponse(serializer.errors, status=400)

    elif request.method == 'DELETE':
        serviceProvider.delete()
        return HttpResponse(status=204)