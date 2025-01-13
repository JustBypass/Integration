package edu.example.testingsystem.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс, отображающий последнюю актуальную статистику по тест-плану.
 * Кроме полей, отвечающих за статистику, имеет единственное поле-первичный ключ test_plan_id,
 *  который ссылается на первичный ключ {@link TestPlan}
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class TestPlanStat {
    @Id
    @Column(name = "test_plan_id")
    private Integer id;

    @OneToOne
    @MapsId//@MapsId annotation, which indicates that the primary key values will be copied from the TestPlan entity.
    @JoinColumn(name = "test_plan_id")
    private TestPlan testPlan;

    private Integer totalTestsAmount;
    private Integer passedTestsAmount;
}
