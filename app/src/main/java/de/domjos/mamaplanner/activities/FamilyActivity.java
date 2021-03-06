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

package de.domjos.mamaplanner.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.turkialkhateeb.materialcolorpicker.ColorChooserDialog;

import java.util.Calendar;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.customwidgets.utils.WidgetUtils;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.mamaplanner.R;
import de.domjos.mamaplanner.model.family.Family;
import de.domjos.mamaplanner.settings.Global;

public final class FamilyActivity extends AbstractActivity {
    private SwipeRefreshDeleteList lvFamily;
    private BottomNavigationView navigation;

    private EditText txtFamilyFirstName, txtFamilyLastName, txtFamilyBirthDate, txtFamilyAlias;
    private TextView lblFamilyColor;
    private Button cmdFamilyColor;
    private Spinner spFamilyGender;
    private ImageButton cmdFamilyCamera, cmdFamilyGallery;

    private Family currentFamily;
    private Validator familyValidator;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_FROM_GALLERY = 2;


    public FamilyActivity() {
        super(R.layout.family_activity);
    }

    @Override
    protected void initActions() {

        this.lvFamily.setOnReloadListener(()->this.manageControls(false, true, false));

        this.lvFamily.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> {
            try {
                this.currentFamily = MainActivity.GLOBAL.getSqLite().getFamily("ID=" + listObject.getId()).get(0);
                this.objectToFields();
                this.manageControls(false, false, true);
            } catch (Exception ex) {
                MessageHelper.printException(ex, R.mipmap.ic_launcher_round, FamilyActivity.this);
            }
        });

        this.lvFamily.setOnDeleteListener(listObject -> {
            try {
                this.currentFamily = MainActivity.GLOBAL.getSqLite().getFamily("ID=" + listObject.getId()).get(0);
                MainActivity.GLOBAL.getSqLite().deleteItem(currentFamily);
                this.manageControls(false, true, false);
            } catch (Exception ex) {
                MessageHelper.printException(ex, R.mipmap.ic_launcher_round, FamilyActivity.this);
            }
        });

        this.cmdFamilyColor.setOnClickListener(view -> {
            ColorChooserDialog cp = new ColorChooserDialog(FamilyActivity.this);
            cp.setColorListener((v, color) -> this.lblFamilyColor.setBackgroundColor(color));
            cp.show();
        });

        this.txtFamilyBirthDate.setOnClickListener(view -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(FamilyActivity.this, R.style.DialogButtonStyled);
                try {
                    String content = this.txtFamilyBirthDate.getText().toString();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(ConvertHelper.convertStringToDate(content, Global.getDateFormat(getApplicationContext())));
                    int year = calendar.get(Calendar.YEAR), month = calendar.get(Calendar.MONTH),
                            day = calendar.get(Calendar.DAY_OF_MONTH);
                    datePickerDialog.updateDate(year, month, day);
                } catch (Exception ignored) {}
                datePickerDialog.setOnDateSetListener((datePicker, i, i1, i2) -> {
                    try {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, i);
                        calendar.set(Calendar.MONTH, i1);
                        calendar.set(Calendar.DAY_OF_MONTH, i2);
                        txtFamilyBirthDate.setText(ConvertHelper.convertDateToString(calendar.getTime(), Global.getDateFormat(getApplicationContext())));
                    } catch (Exception ex) {
                        MessageHelper.printException(ex, R.mipmap.ic_launcher_round, FamilyActivity.this);
                    }
                });
                datePickerDialog.show();
            }
        });

        this.cmdFamilyCamera.setOnClickListener(view -> this.dispatchTakePictureIntent());
        this.cmdFamilyGallery.setOnClickListener(view -> this.dispatchGalleryPictureIntent());
    }

    @Override
    protected void initControls() {
        this.txtFamilyFirstName = this.findViewById(R.id.txtFamilyFirstName);
        this.txtFamilyLastName = this.findViewById(R.id.txtFamilyLastName);
        this.txtFamilyBirthDate = this.findViewById(R.id.txtFamilyBirthDate);
        this.txtFamilyAlias = this.findViewById(R.id.txtFamilyAlias);

        this.lblFamilyColor = this.findViewById(R.id.lblFamilyColor);
        this.cmdFamilyColor = this.findViewById(R.id.cmdFamilyColor);

        this.spFamilyGender = this.findViewById(R.id.spFamilyGender);

        this.cmdFamilyCamera = this.findViewById(R.id.cmdFamilyProfileCamera);
        this.cmdFamilyGallery = this.findViewById(R.id.cmdFamilyProfileGallery);

        this.lvFamily = this.findViewById(R.id.lvFamily);
        this.navigation = this.findViewById(R.id.navigation);

        this.navigation.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.navSysAdd:
                    this.manageControls(true, true, false);
                    break;
                case R.id.navSysEdit:
                    this.manageControls(true, false, false);
                    break;
                case R.id.navSysCancel:
                    this.manageControls(false, false, false);
                    break;
                case R.id.navSysSave:
                    if(this.familyValidator.getState()) {
                        try {
                            this.fieldsToObject();
                            MainActivity.GLOBAL.getSqLite().insertOrUpdateFamily(this.currentFamily);
                            this.manageControls(false, true, false);
                        } catch (Exception ex) {
                            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, FamilyActivity.this);
                        }
                    } else {
                        MessageHelper.printMessage(this.familyValidator.getResult(), R.mipmap.ic_launcher_round, FamilyActivity.this);
                    }
                    break;
            }
            return false;
        });
    }

    @Override
    public void reload() {
        try {
            this.lvFamily.getAdapter().clear();
            for(Family family : MainActivity.GLOBAL.getSqLite().getFamily("")) {
                BaseDescriptionObject listObject = new BaseDescriptionObject();
                listObject.setTitle(String.format("%s %s", family.getFirstName(), family.getLastName()));
                listObject.setId(family.getID());
                listObject.setDescription(ConvertHelper.convertDateToString(family.getBirthDate(), Global.getDateTimeFormat(getApplicationContext())));
                this.lvFamily.getAdapter().add(listObject);
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, FamilyActivity.this);
        }
    }

    @Override
    public void manageControls(boolean editMode, boolean reset, boolean selected) {
        this.txtFamilyFirstName.setEnabled(editMode);
        this.txtFamilyLastName.setEnabled(editMode);
        this.txtFamilyBirthDate.setEnabled(editMode);
        this.txtFamilyAlias.setEnabled(editMode);
        this.cmdFamilyColor.setEnabled(editMode);
        this.spFamilyGender.setEnabled(editMode);
        this.cmdFamilyCamera.setEnabled(editMode);
        this.cmdFamilyGallery.setEnabled(editMode);
        this.lvFamily.setEnabled(!editMode);

        this.navigation.getMenu().findItem(R.id.navSysAdd).setEnabled(!editMode);
        this.navigation.getMenu().findItem(R.id.navSysEdit).setEnabled(!editMode && selected);
        this.navigation.getMenu().findItem(R.id.navSysCancel).setEnabled(editMode);
        this.navigation.getMenu().findItem(R.id.navSysSave).setEnabled(editMode);

        if(reset) {
            this.currentFamily = new Family();
            this.objectToFields();
            this.reload();
        }
    }

    @Override
    public void initValidator() {
        this.familyValidator = new Validator(FamilyActivity.this, R.mipmap.ic_launcher_round);
        this.familyValidator.addEmptyValidator(this.txtFamilyFirstName);
        this.familyValidator.addEmptyValidator(this.txtFamilyBirthDate);
        this.familyValidator.addEmptyValidator(this.txtFamilyAlias);
        this.familyValidator.addDateValidator(this.txtFamilyBirthDate, Global.getDateFormat(getApplicationContext()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                if(extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    if(imageBitmap != null) {
                        this.cmdFamilyCamera.setImageBitmap(imageBitmap);
                        this.cmdFamilyGallery.setImageDrawable(WidgetUtils.getDrawable(FamilyActivity.this, R.drawable.sys_gallery));
                        this.currentFamily.setProfilePicture(ConvertHelper.convertBitmapToByteArray(imageBitmap));
                    }
                }
            }

            if(requestCode == REQUEST_IMAGE_FROM_GALLERY && resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        this.getImageFromGallery(data);
                    } else {
                        ActivityCompat.requestPermissions(FamilyActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                    }
                } else {
                    this.getImageFromGallery(data);
                }
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, FamilyActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            this.dispatchGalleryPictureIntent();
        }
    }

    private void objectToFields() {
        try {
            this.txtFamilyFirstName.setText(this.currentFamily.getFirstName());
            this.txtFamilyLastName.setText(this.currentFamily.getLastName());
            this.txtFamilyAlias.setText(this.currentFamily.getAlias());
            if(this.currentFamily.getBirthDate()!=null) {
                this.txtFamilyBirthDate.setText(ConvertHelper.convertDateToString(this.currentFamily.getBirthDate(), Global.getDateFormat(getApplicationContext())));
            } else {
                this.txtFamilyBirthDate.setText("");
            }
            if(this.currentFamily.getGender()!=null) {
                if(!this.currentFamily.getGender().isEmpty()) {
                    switch (this.currentFamily.getGender().toLowerCase()) {
                        case "m":
                            this.spFamilyGender.setSelection(0);
                            break;
                        case "w":
                            this.spFamilyGender.setSelection(1);
                            break;
                        case "d":
                            this.spFamilyGender.setSelection(2);
                            break;
                    }
                } else {
                    this.spFamilyGender.setSelection(0);
                }
            } else {
                this.spFamilyGender.setSelection(0);
            }
            this.cmdFamilyGallery.setImageDrawable(WidgetUtils.getDrawable(FamilyActivity.this, R.drawable.sys_gallery));
            this.cmdFamilyCamera.setImageDrawable(WidgetUtils.getDrawable(FamilyActivity.this, R.drawable.sys_camera));
            if(this.currentFamily.getColor()==0) {
                this.lblFamilyColor.setBackground(null);
            } else {
                this.lblFamilyColor.setBackgroundColor(this.currentFamily.getColor());
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, FamilyActivity.this);
        }
    }

    private void fieldsToObject() {
        try {
            this.currentFamily.setFirstName(this.txtFamilyFirstName.getText().toString());
            this.currentFamily.setLastName(this.txtFamilyLastName.getText().toString());
            this.currentFamily.setAlias(this.txtFamilyAlias.getText().toString());
            if(!this.txtFamilyBirthDate.getText().toString().isEmpty()) {
                this.currentFamily.setBirthDate(ConvertHelper.convertStringToDate(this.txtFamilyBirthDate.getText().toString(), Global.getDateFormat(getApplicationContext())));
            }
            switch (this.spFamilyGender.getSelectedItemPosition()) {
                case 0:
                    this.currentFamily.setGender("M");
                    break;
                case 1:
                    this.currentFamily.setGender("W");
                    break;
                case 2:
                    this.currentFamily.setGender("D");
                    break;
            }


            try {
                this.currentFamily.setColor(((ColorDrawable) lblFamilyColor.getBackground()).getColor());
            } catch (Exception ignored) {}
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, FamilyActivity.this);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void dispatchGalleryPictureIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_FROM_GALLERY);
    }

    private void getImageFromGallery(Intent data) throws Exception {
        Uri selectedImage = data.getData();
        if(selectedImage != null) {
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if(cursor!=null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                Bitmap bitmap = BitmapFactory.decodeFile(cursor.getString(columnIndex));
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, 256, 256);
                cursor.close();

                if(bitmap != null) {
                    this.cmdFamilyCamera.setImageDrawable(WidgetUtils.getDrawable(FamilyActivity.this, R.drawable.sys_camera));
                    this.cmdFamilyGallery.setImageBitmap(bitmap);
                    this.currentFamily.setProfilePicture(ConvertHelper.convertBitmapToByteArray(bitmap));
                }
            }
        }
    }
}
