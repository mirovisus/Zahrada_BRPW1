package fei.semprace.garden.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gardens")
public class Garden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "area_garden", nullable = false)
    private Double areaGarden;

    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "garden", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GardenImage> images = new ArrayList<>();
    @Column(name = "preview_image_id")
    private Long previewImageId;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "garden", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Request> requests = new ArrayList<>();

    public Garden() {}
    public Garden(String name, Double areaGarden, Address address) {
        this.name = name;
        this.areaGarden = areaGarden;
        this.creationDate = LocalDate.now();
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAreaGarden() {
        return areaGarden;
    }

    public void setAreaGarden(Double areaGarden) {
        this.areaGarden = areaGarden;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Garden{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", areaGarden=" + areaGarden +
                ", creationDate=" + creationDate +
                ", address=" + address +
                '}';
    }

    public List<GardenImage> getImages() {
        return images;
    }

    public void setImages(List<GardenImage> images) {
        this.images = images;
    }

    public Long getPreviewImageId() {
        return previewImageId;
    }

    public void setPreviewImageId(Long previewImageId) {
        this.previewImageId = previewImageId;
    }

    public List<Request> getRequests() {
        return requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }
}
