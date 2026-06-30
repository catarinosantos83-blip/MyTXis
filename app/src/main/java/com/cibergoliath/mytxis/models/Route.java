package com.cibergoliath.mytxis.models;

import java.util.List;

public class Route {

    private OverviewPolyline overview_polyline;

    private List<Leg> legs;

    public OverviewPolyline getOverviewPolyline() {
        return overview_polyline;
    }

    public void setOverviewPolyline(OverviewPolyline overviewPolyline) {
        this.overview_polyline = overviewPolyline;
    }

    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }
}
