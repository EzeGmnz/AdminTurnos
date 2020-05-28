from rest_framework import permissions


class TestPermission(permissions.BasePermission):
    def has_permission(self, request, view):
        return True
