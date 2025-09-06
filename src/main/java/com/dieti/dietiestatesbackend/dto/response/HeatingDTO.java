package com.dieti.dietiestatesbackend.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HeatingDTO {
    private Long id;
    private String type;
    private boolean isActive;
}