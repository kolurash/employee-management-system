package com.employee.employee_management;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class EmployeeCodeService {

    private final ConcurrentHashMap<Integer, AtomicInteger> departmentSequences =
            new ConcurrentHashMap<>();

    public String generateEmployeeCode(Integer departmentId) {

        int number = departmentSequences
                .computeIfAbsent(departmentId, id -> new AtomicInteger(0))
                .incrementAndGet();

        String departmentCode;

        switch (departmentId) {
            case 1:
                departmentCode = "IT";
                break;
            case 2:
                departmentCode = "HR";
                break;
            case 3:
                departmentCode = "FIN";
                break;
            case 4:
                departmentCode = "SAL";
                break;
            case 5:
                departmentCode = "ADM";
                break;
            default:
                departmentCode = "DEP" + departmentId;
        }

        return String.format("%s%03d", departmentCode, number);
    }
}