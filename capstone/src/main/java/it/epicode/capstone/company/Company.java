package it.epicode.capstone.company;

import it.epicode.capstone.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import javax.validation.constraints.Size;

@Entity
@Table(name = "companies")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class Company extends BaseEntity {

    @Size(min = 1, max = 255)
    private String name;
}
