package com.iantmeyer.spyfall.util;

import android.content.Context;
import android.provider.ContactsContract;

import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.iantmeyer.spyfall.game.Contact;

public class PhoneUtil {

    public static boolean isValidNumber(String input) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Iterable<PhoneNumberMatch> numbers = phoneUtil.findNumbers(input, "US");
        if (numbers.iterator().hasNext()) {
            PhoneNumberMatch numberMatch = numbers.iterator().next();
            if (phoneUtil.isValidNumber(numberMatch.number())) {
                return true;
            }
        }
        return false;
    }

    public static String getJustNumber(String input) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Iterable<PhoneNumberMatch> numbers = phoneUtil.findNumbers(input, "US");
        if (numbers.iterator().hasNext()) {
            PhoneNumberMatch numberMatch = numbers.iterator().next();
            if (phoneUtil.isValidNumber(numberMatch.number())) {
                String countryCode = String.valueOf(numberMatch.number().getCountryCode());
                String nationalNumber = String.valueOf(numberMatch.number().getNationalNumber());
                return countryCode + nationalNumber;
            }
        }
        return "";
    }

    public static String displayNumber(Context context, Contact contact) {
        String phone = contact.phone;
        if (phone == null || phone.length() == 0) {
            return "";
        }
        String type = (String) ContactsContract.CommonDataKinds.Phone.getTypeLabel(context.getResources(), contact.type, "Custom");
        if (type.length() > 0) {
            type = "  (" + type + ")";
        }
        phone = phone.replaceAll("[^\\d]", "");
        if (phone.charAt(0) == '1') {
            phone = phone.substring(1);
        }
        if (phone.length() == 10) {
            return "(" + phone.substring(0, 3) + ") " + phone.substring(3, 6) + "-" + phone.substring(6, 10) + type;
        } else if (phone.length() == 7) {
            return phone.substring(0, 3) + "-" + phone.substring(3, 7) + type;
        } else {
            return phone;
        }
    }
}