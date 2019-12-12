/*
 * MamaPlanner
 * Copyright (C) 2019 Domjos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.domjos.mamaplanner.helper;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.mamaplanner.R;
import de.domjos.mamaplanner.settings.Global;

public class Validator {
    private Context context;
    private Map<String, Boolean> states;
    private Map<EditText, Finisher> finishers;
    private final List<String> supportedFormats = Arrays.asList("dd.MM.yyyy", "dd-MM-yyyy", "yyyy-MM-dd", "yyyy.MM.dd", "yyyy/MM/dd", "dd/MM/yyyy");

    public Validator(Context context) {
        this.context = context;
        this.states = new LinkedHashMap<>();
        this.finishers = new LinkedHashMap<>();
    }

    public void addEmptyValidator(EditText txt) {
        this.addStar(txt);
        states.put(txt.getId() + "empty", controlFieldIsEmpty(txt));

        txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                states.put(txt.getId() + "empty", controlFieldIsEmpty(txt));
            }
        });
    }

    public void addValueEqualsRegex(EditText txt, String regex) {
        this.states.put(txt.getId() + "regex", this.controlFieldEqualsRegex(txt, regex));

        txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                states.put(txt.getId() + "regex", controlFieldEqualsRegex(txt, regex));
            }
        });
    }

    public void addValueEqualsDate(EditText txt) {
        this.finishers.put(txt, Finisher.convertDate);
        this.states.put(txt.getId() + "dt", this.controlFieldEqualsDate(txt));

        txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                states.put(txt.getId() + "dt", controlFieldEqualsDate(txt));
            }
        });
    }

    public boolean getState() {
        for(Boolean state : this.states.values()) {
            if(!state) {
                if(this.context instanceof Activity) {
                    ((Activity) this.context).runOnUiThread(() ->
                        MessageHelper.printMessage(this.context.getString(R.string.validator_no_success), R.mipmap.ic_launcher_round, this.context)
                    );
                } else {
                    MessageHelper.printMessage(this.context.getString(R.string.validator_no_success), R.mipmap.ic_launcher_round, this.context);
                }
                return false;
            }
        }
        for(Map.Entry<EditText, Finisher> entry : this.finishers.entrySet()) {
            if(entry.getValue() == Finisher.convertDate) {
                this.executeDateFinisher(entry.getKey());
            }
        }
        return true;
    }

    private enum Finisher {
        convertDate
    }

    private boolean controlFieldIsEmpty(EditText txt) {
        if (txt != null) {
            if (txt.getText() != null) {
                if (txt.getText().toString().isEmpty()) {
                    txt.setError(String.format(this.context.getString(R.string.validator_empty), txt.getHint()));
                    return false;
                } else {
                    txt.setError(null);
                    return true;
                }
            }
        }
        return true;
    }

    private boolean controlFieldEqualsDate(EditText txt) {
        for(String format : this.supportedFormats) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Global.getLocale());
                Date dt = simpleDateFormat.parse(txt.getText().toString());
                if(dt != null) {
                    txt.setError(null);
                    return true;
                }
            } catch (Exception ignored) {}
        }
        txt.setError(String.format(this.context.getString(R.string.validator_noDate), txt.getHint()));
        return false;
    }

    private void executeDateFinisher(EditText txt) {
        for(String format : this.supportedFormats) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Global.getLocale());
                Date dt = simpleDateFormat.parse(txt.getText().toString());
                if(dt != null) {
                    simpleDateFormat = new SimpleDateFormat(Global.getDateFormat(this.context).split(" ")[0], Global.getLocale());
                    txt.setText(simpleDateFormat.format(dt));
                }
            } catch (Exception ignored) {}
        }
    }

    private boolean controlFieldEqualsRegex(EditText txt, String regex) {
        if (txt != null) {
            if (txt.getText() != null) {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(txt.getText().toString());
                if (!matcher.matches()) {
                    txt.setError(String.format(this.context.getString(R.string.validator_matches), txt.getHint()));
                    return false;
                }
            }
            txt.setError(null);
        }
        return true;
    }

    private void addStar(EditText txt) {
        String hint = txt.getHint().toString();
        if (!hint.endsWith(" *")) {
            txt.setHint(hint + " *");
        }
    }
}
