package com.cagatay.DemoProject.bean;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.cagatay.DemoProject.dao.EventDAO;
import com.cagatay.DemoProject.dao.UserDAO;
import com.cagatay.DemoProject.entities.Event;
import com.cagatay.DemoProject.entities.User;
import com.cagatay.DemoProject.util.Util;

import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

@ViewScoped
@ManagedBean(name = "eventBean")
public class EventBean {
	private EventDAO eventDAO;

	private Event eventEntity;

	private Long eventEntityId;

	private UserDAO userDAO;

	private User currentUser;

	private List<Event> userEvents;

	private HashMap<Long, ScheduleEvent<Object>> eventsMap;

	private ScheduleModel eventModel;

	private ScheduleEvent<Object> event;

	public EventBean() {
		event = new DefaultScheduleEvent<Object>();
		userDAO = new UserDAO();
		this.currentUser = userDAO.getUserByName(Util.getUserName());
		eventModel = new DefaultScheduleModel();
		eventDAO = new EventDAO();
		eventsMap = new HashMap<Long, ScheduleEvent<Object>>();
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		userEvents = eventDAO.getUserEvents(this.currentUser.getId());

		for (Event e : userEvents) {
			@SuppressWarnings("rawtypes")
			DefaultScheduleEvent event = DefaultScheduleEvent.builder().title(e.getTitle()).startDate(e.getFromDate())
					.endDate(e.getToDate()).overlapAllowed(true).allDay(e.isAllDay()).build();
			eventModel.addEvent(event);
			eventsMap.put(e.getId(), event);
		}
	}

	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public ScheduleEvent<Object> getEvent() {
		return event;
	}

	public void setEvent(ScheduleEvent<Object> event) {
		this.event = event;
	}

	public void addEvent() {
		if (event.isAllDay()) {
			// see https://github.com/primefaces/primefaces/issues/1164
			if (event.getStartDate().toLocalDate().equals(event.getEndDate().toLocalDate())) {
				event.setEndDate(event.getEndDate().plusDays(1));
			}
		}
		eventEntity = getNewEvent(event);
		if (event.getId() == null) {
			eventModel.addEvent(event);
			eventDAO.create(eventEntity);
		} else {
			eventModel.updateEvent(event);
			eventEntity.setId(eventEntityId);
			eventDAO.update(eventEntity);
		}

		event = new DefaultScheduleEvent<Object>();
	}

	public void deleteEvent() {
		eventEntity.setId(eventEntityId);
		eventDAO.delete(eventEntity);
		eventModel.deleteEvent(event);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onEventSelect(SelectEvent<ScheduleEvent> selectEvent) {
		event = selectEvent.getObject();
		eventEntityId = getIdByScheduleEvent(eventsMap, event);
		eventEntity = getNewEvent(event);
	}

	@SuppressWarnings("unchecked")
	public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {
		event = DefaultScheduleEvent.builder().startDate(selectEvent.getObject())
				.endDate(selectEvent.getObject().plusHours(1)).build();
	}

	public void onEventMove(ScheduleEntryMoveEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved",
				"Delta:" + event.getDeltaAsDuration());

		addMessage(message);
	}

	public void onEventResize(ScheduleEntryResizeEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized",
				"Start-Delta:" + event.getDeltaStartAsDuration() + ", End-Delta: " + event.getDeltaEndAsDuration());

		addMessage(message);
	}

	private void addMessage(FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public Event getNewEvent(ScheduleEvent<Object> event) {
		Event newEvent = new Event();
		newEvent.setTitle(event.getTitle());
		newEvent.setAllDay(event.isAllDay());
		newEvent.setDescription(event.getDescription());
		newEvent.setFromDate(event.getStartDate());
		newEvent.setToDate(event.getEndDate());
		newEvent.setUser(this.currentUser);
		return newEvent;
	}

	public Long getIdByScheduleEvent(HashMap<Long, ScheduleEvent<Object>> eventsMap, ScheduleEvent<Object> value) {
		for (Entry<Long, ScheduleEvent<Object>> entry : eventsMap.entrySet()) {
			if (Objects.equals(value, entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}
}
