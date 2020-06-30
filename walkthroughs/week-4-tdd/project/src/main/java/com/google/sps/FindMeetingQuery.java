// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;

public final class FindMeetingQuery {
  private boolean intersects(Collection<String> requestAttendees, Set<String> eventAttendees) {
      // a function that tests if the attendees of an event intersect with the request's
      // attendees. On^2
     for(String eventAttendee : eventAttendees) {
        for(String requestAttendee : requestAttendees) {
            if(requestAttendee.equals(eventAttendee)) {
                return false;
            }
        }
     }
     return false;
  }
  private Collection<Event> filterAttendeesIntersect(Collection<Event> events, MeetingRequest request) {
      // a function that takes a collection of events and a meeting request
      // it returns a collection of events where the events' attendees intersect with
      // the attendees of the meeting request

      Collection<Event> filtered = new ArrayList<Event>();
      Collection<String> requestAttendees = request.getAttendees();

      for(Event event : events) {
        Set<String> eventAttendees = event.getAttendees();
        if(intersects(requestAttendees, eventAttendees)) {
            filtered.add(event);
        }
      }
      return filtered;
  }
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // a method that returns a collection of timeranges where people are free to meet
    // event: a period of time where people are busy
    // "events" contains data that referring to when people are busy
    if(events == null || request == null) { // null case check
        throw new UnsupportedOperationException("Null Parameter 'events' or 'reqest'.");
    }
    // initialize return value
    Collection<TimeRange> openings = new ArrayList<TimeRange>();

    // filter out events that do not pertain to the people in our meeting request
    Collection<Event> filteredEvents = filterAttendeesIntersect(events, request);

    if(filteredEvents.isEmpty()){ // edge case: events is empty; all day is open
      openings.add(TimeRange.WHOLE_DAY);
      return openings;
    }

    // Extract busy TimeRanges
    ArrayList<TimeRange> eventTimes = new ArrayList<TimeRange>();
    for(Event event : filteredEvents) {
        eventTimes.add(event.getWhen());
    }

    // sort busy TimeRanges
    Collections.sort(eventTimes, TimeRange.ORDER_BY_START);
    Iterator<TimeRange> timeIterator = eventTimes.iterator();

    TimeRange temp = timeIterator.next();

    // edge case: start of day
    if(temp.start() < TimeRange.START_OF_DAY) {
      openings.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, temp.start(), false));
    }

    // for every TimeRange
    while(timeIterator.hasNext()) {
      // compute end time of current TimeRange
      int endTime = temp.start() + temp.duration();
      // retrieve the start time of next TimeRange 
      // (recall that they are sorted and that Collections does not store duplicates)
      temp = timeIterator.next();
      int nextStart = temp.start();

      if(endTime < nextStart) {
        openings.add(TimeRange.fromStartDuration(endTime, nextStart - endTime));
      }
    }
    
    // edge case: end of day
    if(temp.start() < TimeRange.END_OF_DAY) {
      int startTime = temp.start() + temp.duration();
      openings.add(TimeRange.fromStartDuration(startTime, TimeRange.END_OF_DAY - startTime));
    }

    return openings; // question: do we want to return empty if no openings exist?
    // algorithm works as follows:
    // check null cases
    // filter events s.t. we only test for events that pertain to the attendees in
    //   the request
    // sort events by start time
    // for every event:
    //   calculate end time : event start time + event duration
    //   if end time < start time of next event
            // create a time range for the end time + () and add it to the return value
    // return the collection of time ranges return value
  }
}
