package com.iteam.dto;

import lombok.*;

import java.util.List;
@Data
@ToString
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CreateCommandeRequestDTO {

    private Long userId;
    private List<Long> productsId;



}
