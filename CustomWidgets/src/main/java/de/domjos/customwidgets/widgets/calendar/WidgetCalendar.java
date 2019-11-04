package de.domjos.customwidgets.widgets.calendar;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.gridlayout.widget.GridLayout;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.domjos.customwidgets.R;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.WidgetUtils;

public class WidgetCalendar extends LinearLayout {
    private Context context;
    private ImageButton cmdCalSkipNext, cmdCalNext, cmdCalPrevious, cmdCalSkipPrevious;
    private TextView lblCalDate;
    private GridLayout gridLayout;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat dateFormatWithDay;
    private List<Map.Entry<String, Event>> events;
    private ClickListener clickListener;
    private Event currentEvent;

    public WidgetCalendar(Context context) {
        super(context);

        this.initDefaults(context);
        this.initControls();
        this.initActions();
    }

    public WidgetCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.initDefaults(context);
        this.initControls();
        this.initActions();
    }

    public void addEvent(Event event) {
        if(event.getCalendar() != null) {
            this.events.add(new AbstractMap.SimpleEntry<>(this.dateFormatWithDay.format(event.getCalendar().getTime()), event));
        }
    }

    public Event getCurrentEvent() {
        return this.currentEvent;
    }

    @SuppressWarnings("unused")
    public List<Map.Entry<String, Event>> getEvents() {
        return this.events;
    }

    public void setOnClick(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void reload() {
        this.reloadCalendar();
    }

    private void initActions() {

        this.cmdCalNext.setOnClickListener(view -> {
            try {
                Date date = this.dateFormat.parse(this.lblCalDate.getText().toString());
                if(date!=null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
                    this.lblCalDate.setText(this.dateFormat.format(calendar.getTime()));
                    this.reloadCalendar();
                }
            } catch (Exception ex) {
                MessageHelper.printException(ex, this.context);
            }
        });

        this.cmdCalSkipNext.setOnClickListener(view -> {
            try {
                Date date = this.dateFormat.parse(this.lblCalDate.getText().toString());
                if(date!=null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
                    this.lblCalDate.setText(this.dateFormat.format(calendar.getTime()));
                    this.reloadCalendar();
                }
            } catch (Exception ex) {
                MessageHelper.printException(ex, this.context);
            }
        });

        this.cmdCalPrevious.setOnClickListener(view -> {
            try {
                Date date = this.dateFormat.parse(this.lblCalDate.getText().toString());
                if(date!=null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                    this.lblCalDate.setText(this.dateFormat.format(calendar.getTime()));
                    this.reloadCalendar();
                }
            } catch (Exception ex) {
                MessageHelper.printException(ex, this.context);
            }
        });

        this.cmdCalSkipPrevious.setOnClickListener(view -> {
            try {
                Date date = this.dateFormat.parse(this.lblCalDate.getText().toString());
                if(date!=null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
                    this.lblCalDate.setText(this.dateFormat.format(calendar.getTime()));
                    this.reloadCalendar();
                }
            } catch (Exception ex) {
                MessageHelper.printException(ex, this.context);
            }
        });
    }

    private void initDefaults(Context context) {
        this.context = context;
        this.setOrientation(VERTICAL);

        this.events = new LinkedList<>();
    }

    @SuppressWarnings("deprecation")
    private void initControls() {
        this.dateFormat = new SimpleDateFormat("MM.yyyy", Locale.getDefault());
        this.dateFormatWithDay = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        LinearLayout linearLayout = new LinearLayout(this.context);
        linearLayout.setOrientation(HORIZONTAL);
        linearLayout.setWeightSum(10);
        this.addView(linearLayout);

        this.cmdCalSkipPrevious = new ImageButton(this.context);
        this.cmdCalSkipPrevious.setImageDrawable(WidgetUtils.getDrawable(this.context, R.drawable.ic_skip_previous));
        this.cmdCalSkipPrevious.setLayoutParams(this.getLayoutParamsByWeight(1, linearLayout));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.cmdCalSkipPrevious.setBackground(null);
        } else {
            this.cmdCalSkipPrevious.setBackgroundDrawable(null);
        }
        linearLayout.addView(this.cmdCalSkipPrevious);

        this.cmdCalPrevious = new ImageButton(this.context);
        this.cmdCalPrevious.setImageDrawable(WidgetUtils.getDrawable(this.context, R.drawable.ic_previous));
        this.cmdCalPrevious.setLayoutParams(this.getLayoutParamsByWeight(1, linearLayout));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.cmdCalPrevious.setBackground(null);
        } else {
            this.cmdCalPrevious.setBackgroundDrawable(null);
        }
        linearLayout.addView(this.cmdCalPrevious);

        this.lblCalDate = new TextView(this.context);
        this.lblCalDate.setText(this.dateFormat.format(new Date()));
        LayoutParams layoutParams = this.getLayoutParamsByWeight(6, linearLayout);
        layoutParams.gravity = Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL;
        this.lblCalDate.setLayoutParams(layoutParams);
        this.lblCalDate.setGravity(Gravity.CENTER);
        this.lblCalDate.setTextSize(24);
        this.lblCalDate.setTypeface(null, Typeface.BOLD);
        linearLayout.addView(this.lblCalDate);

        this.cmdCalNext = new ImageButton(this.context);
        this.cmdCalNext.setImageDrawable(WidgetUtils.getDrawable(this.context, R.drawable.ic_next));
        this.cmdCalNext.setLayoutParams(this.getLayoutParamsByWeight(1, linearLayout));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.cmdCalNext.setBackground(null);
        } else {
            this.cmdCalNext.setBackgroundDrawable(null);
        }
        linearLayout.addView(this.cmdCalNext);

        this.cmdCalSkipNext = new ImageButton(this.context);
        this.cmdCalSkipNext.setImageDrawable(WidgetUtils.getDrawable(this.context, R.drawable.ic_skip_next));
        this.cmdCalSkipNext.setLayoutParams(this.getLayoutParamsByWeight(1, linearLayout));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.cmdCalSkipNext.setBackground(null);
        } else {
            this.cmdCalSkipNext.setBackgroundDrawable(null);
        }
        linearLayout.addView(this.cmdCalSkipNext);

        TextView textView = new TextView(this.context);
        LinearLayout.LayoutParams tmp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        textView.setLayoutParams(tmp);
        textView.setBackgroundColor(WidgetUtils.getColor(this.context, android.R.color.background_dark));
        this.addView(textView);

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this.context);
        horizontalScrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.addView(horizontalScrollView);

        this.gridLayout = new GridLayout(this.context);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.gridLayout.setLayoutParams(layout);
        this.gridLayout.setColumnCount(7);
        this.gridLayout.setOrientation(GridLayout.HORIZONTAL);
        this.gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        horizontalScrollView.addView(this.gridLayout);
        this.reloadCalendar();
    }

    private void addDaysOfWeek() {
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(Locale.getDefault());
        for(String dayOfWeek : dateFormatSymbols.getWeekdays()) {
            if(dayOfWeek.length()>=3) {
                this.gridLayout.addView(this.addTextView(dayOfWeek.substring(0, 3)));
            }
        }
    }

    private void reloadCalendar() {
        try {
            this.gridLayout.removeAllViews();
            this.addDaysOfWeek();

            Calendar calendar = this.getDefaultCalendar();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            for(int i = day-1; i>0; i--) {
                this.gridLayout.addView(this.addTextView(""));
            }
            for(int i = 1; i<=calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                this.gridLayout.addView(this.addDay(i));
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, this.context);
        }
    }

    private TextView addTextView(String text) {
        TextView lbl = new TextView(this.context);
        lbl.setText(text);
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.setGravity(Gravity.CENTER_HORIZONTAL);
        layoutParams.setMargins(5, 5, 5, 5);
        layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
        lbl.setLayoutParams(layoutParams);
        lbl.setGravity(Gravity.CENTER_HORIZONTAL);
        lbl.setTextSize(20);
        lbl.setPadding(5, 5, 5, 5);
        lbl.setTypeface(null, Typeface.BOLD);
        return lbl;
    }

    @SuppressWarnings("deprecation")
    private LinearLayout addDay(int currentDayOfMonth) {
        Calendar calendar = this.getDefaultCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, currentDayOfMonth);
        String strDate = this.dateFormatWithDay.format(calendar.getTime());
        List<Event> eventsOnDay = new LinkedList<>();
        for(Map.Entry<String, Event> entry : this.events) {
            if(entry.getKey().equals(strDate)) {
                eventsOnDay.add(entry.getValue());
            }
        }

        LinearLayout linearLayout = new LinearLayout(this.context);
        linearLayout.setOrientation(VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.setGravity(Gravity.CENTER_HORIZONTAL);
        layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
        layoutParams.setMargins(5, 5, 5, 5);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setPadding(5, 5, 5, 5);
        linearLayout.addView(this.addTextViewToDay(String.valueOf(currentDayOfMonth), android.R.color.transparent));
        linearLayout.setOnClickListener(view -> {
            for(int i = 0; i<=this.gridLayout.getChildCount()-1; i++) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    this.gridLayout.getChildAt(i).setBackground(WidgetUtils.getDrawable(this.context, -1));
                } else {
                    this.gridLayout.getChildAt(i).setBackgroundDrawable(WidgetUtils.getDrawable(this.context, -1));
                }
            }
            linearLayout.setBackgroundColor(WidgetUtils.getColor(this.context, android.R.color.darker_gray));

            Event event = new Event();
            event.setCalendar(calendar.getTime());
            this.currentEvent = event;
        });


        for(Event event : eventsOnDay) {
            TextView textView = this.addTextViewToDay(event.getName(), event.getColor());
            ((LayoutParams) textView.getLayoutParams()).setMargins(2, 2, 2, 5);
            textView.setPadding(2, 2, 2, 2);
            textView.setOnClickListener(v -> {
                this.currentEvent = event;
                if(this.clickListener != null) {
                    this.clickListener.onClick(event);
                }
            });
            linearLayout.addView(textView);
        }
        return linearLayout;
    }

    private TextView addTextViewToDay(String content, int color) {
        TextView textView = new TextView(this.context);
        textView.setText(content);
        textView.setBackgroundColor(WidgetUtils.getColor(this.context, color));
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return textView;
    }

    private LayoutParams getLayoutParamsByWeight(float weight, LinearLayout linearLayout) {
        if(linearLayout.getOrientation()==HORIZONTAL) {
            return new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, weight);
        } else {
            return new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0, weight);
        }
    }

    private Calendar getDefaultCalendar() {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        try {
            Date date = this.dateFormat.parse(this.lblCalDate.getText().toString());
            if(date != null) {
                calendar.setTime(date);
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, this.context);
        }
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        return calendar;
    }

    public abstract static class ClickListener {
        public abstract void onClick(Event event);
    }
}
