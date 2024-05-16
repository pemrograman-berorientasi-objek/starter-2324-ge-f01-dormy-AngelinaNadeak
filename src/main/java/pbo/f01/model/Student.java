package pbo.f01.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @Column(name = "id", nullable = false, length = 20)
    private String id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "batch", nullable = false)
    private Integer batch;

    @Column(name = "gender", nullable = false, length = 10)
    private String gender;

    @ManyToOne
    @JoinColumn(name = "dorm_id")
    private Dorm dorm;

    public Student() {
        // empty
    }

    public Student(String id, String name, Integer batch, String gender) {
        this.id = id;
        this.name = name;
        this.batch = batch;
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBatch() {
        return batch;
    }

    public void setBatch(Integer batch) {
        this.batch = batch;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Dorm getDorm() {
        return dorm;
    }

    public void setDorm(Dorm dorm) {
        this.dorm = dorm;
    }

    @Override
    public String toString() {
        return id + "|" + name + "|" + batch;
    }
}
