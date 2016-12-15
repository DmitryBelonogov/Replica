package com.nougust3.diary;

import com.p_v.flexiblecalendar.entity.Event;

/**
 * Created by Perceval Balonezov on 8.12.16.
 */
public class CustomEvent implements Event {

    private int color;

    public CustomEvent(int color){
        this.color = color;
    }

    @Override
    public int getColor() {
        return color;
    }
}