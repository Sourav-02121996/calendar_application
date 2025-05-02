package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import java.time.YearMonth;
import java.time.Month;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

/**
 * A Java class that represents a date picker component for viewing days in a calendar month.
 * Extends the JPanel class from Java Swing.
 */
public class CustomDatePicker extends JPanel {

  private final JLabel monthLabel;
  private final JPanel daysPanel;
  private final Map<Integer, JButton> buttonMap;

  private final IViewModel viewModel;
  private final CalendarFrame calendarFrame;
  private final EventsTable eventsTable;

  private int currentMonth;
  private int currentYear;
  private final Calendar selectedCalendar;

  /**
   * Constructs a CustomDatePicker object. Calls the JPanel constructor.
   *
   * @param viewModel     IViewModel object
   * @param eventsTable   EventsTable object
   * @param calendarFrame CalendarFrame object
   */
  public CustomDatePicker(IViewModel viewModel, EventsTable eventsTable,
      CalendarFrame calendarFrame) {
    super();
    this.eventsTable = eventsTable;
    this.viewModel = viewModel;
    this.calendarFrame = calendarFrame;

    setLayout(new BorderLayout());

    selectedCalendar = getCalendar(viewModel.getCurrentDate());
    currentYear = selectedCalendar.get(Calendar.YEAR);
    currentMonth = selectedCalendar.get(Calendar.MONTH) + 1;

    Dimension navButtonSize = new Dimension(45, 25);
    viewModel.setSelectedDate(selectedCalendar.getTime());

    JButton prevYearButton = new JButton("<<");
    prevYearButton.setFont(prevYearButton.getFont().deriveFont(10f));
    prevYearButton.setPreferredSize(navButtonSize);
    prevYearButton.addActionListener(e -> {
      currentYear--;
      updateDays();
    });

    JButton prevMonthButton = new JButton("<");
    prevMonthButton.setFont(prevMonthButton.getFont().deriveFont(10f));
    prevMonthButton.setPreferredSize(navButtonSize);
    prevMonthButton.addActionListener(e -> {
      if (currentMonth == 1) {
        currentMonth = 12;
        currentYear--;
      } else {
        currentMonth--;
      }
      updateDays();
    });

    monthLabel = new JLabel();
    updateMonthLabel();

    JButton nextMonthButton = new JButton(">");
    nextMonthButton.setFont(nextMonthButton.getFont().deriveFont(10f));
    nextMonthButton.setPreferredSize(navButtonSize);
    nextMonthButton.addActionListener(e -> {
      if (currentMonth == 12) {
        currentMonth = 1;
        currentYear++;
      } else {
        currentMonth++;
      }
      updateDays();
    });

    JButton nextYearButton = new JButton(">>");
    nextYearButton.setFont(nextYearButton.getFont().deriveFont(10f));
    nextYearButton.setPreferredSize(navButtonSize);
    nextYearButton.addActionListener(e -> {
      currentYear++;
      updateDays();
    });

    JPanel monthNavPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    monthNavPanel.add(prevYearButton);
    monthNavPanel.add(prevMonthButton);
    monthNavPanel.add(monthLabel);
    monthNavPanel.add(nextMonthButton);
    monthNavPanel.add(nextYearButton);

    add(monthNavPanel, BorderLayout.NORTH);

    // 7 columns, dynamic rows
    daysPanel = new JPanel(new GridLayout(0, 7, 5, 5));
    add(daysPanel, BorderLayout.CENTER);

    buttonMap = new HashMap<>();
    updateDays();
  }

  private void updateMonthLabel() {
    Month monthEnum = Month.of(currentMonth);
    monthLabel.setText(monthEnum.toString().substring(0, 1).toUpperCase()
        + monthEnum.toString().substring(1).toLowerCase() + " " + currentYear);
  }

  private void updateDays() {
    daysPanel.removeAll();
    updateMonthLabel();

    String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    for (String dayName : dayNames) {
      JLabel label = new JLabel(dayName, SwingConstants.CENTER);
      label.setFont(label.getFont().deriveFont(Font.BOLD));
      daysPanel.add(label);
    }

    // create offset based on first day of month
    int startOffset = getFirstDayOfWeek() % 7;
    for (int i = 0; i < startOffset; i++) {
      daysPanel.add(new JLabel(""));
    }

    // create buttons for all days in calendar
    int daysInMonth = YearMonth.of(currentYear, currentMonth).lengthOfMonth();
    for (int day = 1; day <= daysInMonth; day++) {
      JButton dayButton = new JButton(String.valueOf(day));
      dayButton.setOpaque(true);
      dayButton.setActionCommand(String.valueOf(day));
      buttonMap.put(day, dayButton);

      // set selected by default
      Date date = getDate(currentYear, currentMonth - 1, day);
      if (date.equals(selectedCalendar.getTime())) {
        setSelected(dayButton, true);
      }

      // add action listener to each button
      dayButton.addActionListener(e -> {
        // deselect current day
        int currentDay = selectedCalendar.get(Calendar.DAY_OF_MONTH);
        setSelected(buttonMap.get(currentDay), false);

        // set new selected date
        currentDay = Integer.parseInt(e.getActionCommand());
        setSelected(buttonMap.get(currentDay), true);
        Date selectedDate = getDate(currentYear, currentMonth - 1, currentDay);

        selectedCalendar.setTime(selectedDate);
        viewModel.setSelectedDate(selectedDate);
        eventsTable.updateTableModel(selectedDate, calendarFrame);
      });
      daysPanel.add(dayButton);
    }
    daysPanel.revalidate();
    daysPanel.repaint();
  }

  private void setSelected(JButton dayButton, boolean selected) {
    dayButton.setSelected(selected);
    if (selected) {
      dayButton.setBackground(Color.BLUE);
    } else {
      dayButton.setBackground(null);
    }
  }

  private Date getDate(int year, int month, int day) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month);
    calendar.set(Calendar.DAY_OF_MONTH, day);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  private int getFirstDayOfWeek() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, currentYear);
    calendar.set(Calendar.MONTH, currentMonth - 1);
    calendar.set(Calendar.DAY_OF_MONTH, 0);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.get(Calendar.DAY_OF_WEEK);
  }

  private Calendar getCalendar(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar;
  }
}
