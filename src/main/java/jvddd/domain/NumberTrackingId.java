package jvddd.domain;


import javax.persistence.Column;

/**
 * 实现自动增长主键规则
 * Created by Max on 2015/12/25.
 */
public class NumberTrackingId extends TrackingId<Long> {
    @Column(name = "id")
    protected Long id;

    public NumberTrackingId() {
    }

    public NumberTrackingId(Long id) {
        this.id = id;
    }

    @Override
    public boolean sameAs(TrackingId other) {
        return other != null && this.id() == other.id();
    }

    @Override
    public Long id() {
        return id;
    }
}
