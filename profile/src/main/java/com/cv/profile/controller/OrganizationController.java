package com.cv.profile.controller;

import com.cv.profile.dto.request.OrganizationRequest.*;
import com.cv.profile.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/org") // Prefix chung cho tổ chức
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrganizationController {

    private final OrganizationService organizationService;

    // --- REGION APIs ---
    @GetMapping("/regions")
    public ResponseEntity<?> getAllRegions() {
        return ResponseEntity.ok(organizationService.getAllRegions());
    }

    @PostMapping("/regions")
    public ResponseEntity<?> createRegion(@RequestBody RegionRequest req) {
        return ResponseEntity.ok(organizationService.createRegion(req));
    }

    @PutMapping("/regions/{id}")
    public ResponseEntity<?> updateRegion(@PathVariable Long id, @RequestBody RegionRequest req) {
        return ResponseEntity.ok(organizationService.updateRegion(id, req));
    }

    @DeleteMapping("/regions/{id}")
    public ResponseEntity<?> deleteRegion(@PathVariable Long id) {
        organizationService.deleteRegion(id);
        return ResponseEntity.ok("Deleted Region " + id);
    }

    // --- LOCAL ORG APIs ---
    @GetMapping("/locals")
    public ResponseEntity<?> getAllLocals() {
        return ResponseEntity.ok(organizationService.getAllLocalOrgs());
    }

    @PostMapping("/locals")
    public ResponseEntity<?> createLocal(@RequestBody LocalOrgRequest req) {
        try {
            return ResponseEntity.ok(organizationService.createLocalOrg(req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/locals/{id}")
    public ResponseEntity<?> updateLocal(@PathVariable Long id, @RequestBody LocalOrgRequest req) {
        return ResponseEntity.ok(organizationService.updateLocalOrg(id, req));
    }

    @DeleteMapping("/locals/{id}")
    public ResponseEntity<?> deleteLocal(@PathVariable Long id) {
        organizationService.deleteLocalOrg(id);
        return ResponseEntity.ok("Deleted Local Org " + id);
    }

    // --- DEPARTMENT APIs ---
    @GetMapping("/departments")
    public ResponseEntity<?> getAllDepartments() {
        return ResponseEntity.ok(organizationService.getAllDepartments());
    }

    @PostMapping("/departments")
    public ResponseEntity<?> createDepartment(@RequestBody DepartmentRequest req) {
        try {
            return ResponseEntity.ok(organizationService.createDepartment(req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/departments/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable Long id, @RequestBody DepartmentRequest req) {
        return ResponseEntity.ok(organizationService.updateDepartment(id, req));
    }

    @DeleteMapping("/departments/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        organizationService.deleteDepartment(id);
        return ResponseEntity.ok("Deleted Department " + id);
    }
}
