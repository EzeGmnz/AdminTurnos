package com.adminturnos.Builder;

import com.adminturnos.ObjectInterfaces.Appointment;
import com.adminturnos.ObjectInterfaces.CustomUser;
import com.adminturnos.ObjectInterfaces.Service;
import com.adminturnos.ObjectInterfaces.ServiceInstance;
import com.adminturnos.Objects.ServiceAppointment;
import com.adminturnos.Objects.ServiceAppointmentInstance;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BuilderListAppointment {

    public List<Appointment> build(JSONObject json) throws JSONException {
        List<Appointment> out = new ArrayList<>();

        Map<String, Service> serviceMap = new HashMap<>();
        Appointment appointment;
        ServiceInstance serviceInstance;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);

        for (Iterator<String> itAppointments = json.keys(); itAppointments.hasNext(); ) {
            String appointmentId = itAppointments.next();
            JSONObject jsonAppointmentData = json.getJSONObject(appointmentId);

            JSONObject jsonAppointment = jsonAppointmentData.getJSONObject("appointment");
            JSONObject jsonServices = jsonAppointmentData.getJSONObject("services");

            Calendar appointmentDate = Calendar.getInstance();
            try {
                appointmentDate.setTime(dateFormat.parse(jsonAppointment.getString("date")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            CustomUser client = new BuilderObjectCustomUser().build(jsonAppointment.getJSONObject("client"));


            List<ServiceInstance> serviceInstanceList = new ArrayList<>();
            for (Iterator<String> itServices = jsonServices.keys(); itServices.hasNext(); ) {
                String serviceNumber = itServices.next();

                JSONObject jsonServiceData = jsonServices.getJSONObject(serviceNumber);
                String serviceInstanceId = jsonServiceData.getString("id");
                Calendar serviceInstanceDateTime = Calendar.getInstance();

                try {
                    serviceInstanceDateTime.setTime(dateTimeFormat.parse(jsonServiceData.getString("timestamp")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String serviceId = jsonServiceData.getJSONObject("service").getString("id");

                Service service;
                if (!serviceMap.containsKey(serviceId)) {
                    JSONObject serviceData = jsonServiceData.getJSONObject("service");
                    service = new BuilderObjectService().build(serviceData);
                    serviceMap.put(serviceId, service);
                } else {
                    service = serviceMap.get(serviceId);
                }

                serviceInstance = new ServiceAppointmentInstance(
                        serviceInstanceId,
                        serviceInstanceDateTime,
                        service

                );
                serviceInstanceList.add(serviceInstance);
            }
            appointment = new ServiceAppointment(client, appointmentId, appointmentDate, serviceInstanceList);
            out.add(appointment);
        }
        return out;
    }

}
