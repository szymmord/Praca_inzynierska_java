package com.example.projekt_inzynierski;

import java.util.List;

public class pollen {
    public class DailyInfo {
        private Date date;
        private List<PollenTypeInfo> pollenTypeInfo;

        // Dodaj konstruktory, gettery i settery

        public Date getDate() {
            return date;
        }

        public List<PollenTypeInfo> getPollenTypeInfo() {
            return pollenTypeInfo;
        }
    }

    public class Date {
        private int year;
        private int month;
        private int day;

        // Dodaj konstruktory, gettery i settery

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        public int getDay() {
            return day;
        }
    }

    public class PollenTypeInfo {
        private String code;
        private String displayName;
        private boolean inSeason;
        private IndexInfo indexInfo;
        private List<String> healthRecommendations;

        // Dodaj konstruktory, gettery i settery

        public String getCode() {
            return code;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isInSeason() {
            return inSeason;
        }

        public IndexInfo getIndexInfo() {
            return indexInfo;
        }

        public List<String> getHealthRecommendations() {
            return healthRecommendations;
        }
    }

    public class IndexInfo {
        private String code;
        private String displayName;
        private int value;
        private String category;
        private String indexDescription;
        private Color color;

        // Dodaj konstruktory, gettery i settery

        public String getCode() {
            return code;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getValue() {
            return value;
        }

        public String getCategory() {
            return category;
        }

        public String getIndexDescription() {
            return indexDescription;
        }

        public Color getColor() {
            return color;
        }
    }

    public class Color {
        private double green;
        private double blue;

        // Dodaj konstruktory, gettery i settery

        public double getGreen() {
            return green;
        }

        public double getBlue() {
            return blue;
        }
    }

}
