package com.cv.profile.dto.ai;

import java.util.List;

public record CVExtractionResult(
                BilingualField fullName,
                String email,
                String phone,
                String linkedin,
                String github,
                String avatarUrl,

                // Các trường song ngữ
                BilingualField jobTitle,
                BilingualField bio,
                BilingualField address,

                // Lists
                List<AiExperience> workHistory,
                List<AiProject> projects,
                List<AiSkill> skills,
                List<AiEducation> education,
                List<AiPublication> publications,
                List<AiEvent> events) {

        // Record con chứa 2 ngôn ngữ
        public record BilingualField(String vi, String en) {
        }

        public record AiExperience(
                        String companyName,
                        BilingualField position,
                        String startDate,
                        String endDate,
                        BilingualField description) {
        }

        public record AiProject(
                        BilingualField name,
                        BilingualField role,
                        BilingualField description,
                        String sourceCodeUrl,
                        List<String> techStack) {
        }

        public record AiSkill(
                        String name,
                        String category,
                        Integer proficiency) {
        }

        public record AiEducation(
                        String school,
                        BilingualField degree,
                        String startDate,
                        String endDate,
                        BilingualField description) {
        }

        public record AiPublication(
                        BilingualField name,
                        String publisher,
                        String date,
                        String url) {
        }

        public record AiEvent(
                        BilingualField name,
                        String role,
                        String date,
                        BilingualField description) {
        }
}