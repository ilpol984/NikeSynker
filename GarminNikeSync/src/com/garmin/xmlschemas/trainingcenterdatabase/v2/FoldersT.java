//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.07.30 at 03:35:02 PM CEST 
//


package com.garmin.xmlschemas.trainingcenterdatabase.v2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Folders_t complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Folders_t">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="History" type="{http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2}History_t" minOccurs="0"/>
 *         &lt;element name="Workouts" type="{http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2}Workouts_t" minOccurs="0"/>
 *         &lt;element name="Courses" type="{http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2}Courses_t" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Folders_t", propOrder = {
    "history",
    "workouts",
    "courses"
})
public class FoldersT {

    @XmlElement(name = "History")
    protected HistoryT history;
    @XmlElement(name = "Workouts")
    protected WorkoutsT workouts;
    @XmlElement(name = "Courses")
    protected CoursesT courses;

    /**
     * Gets the value of the history property.
     * 
     * @return
     *     possible object is
     *     {@link HistoryT }
     *     
     */
    public HistoryT getHistory() {
        return history;
    }

    /**
     * Sets the value of the history property.
     * 
     * @param value
     *     allowed object is
     *     {@link HistoryT }
     *     
     */
    public void setHistory(HistoryT value) {
        this.history = value;
    }

    /**
     * Gets the value of the workouts property.
     * 
     * @return
     *     possible object is
     *     {@link WorkoutsT }
     *     
     */
    public WorkoutsT getWorkouts() {
        return workouts;
    }

    /**
     * Sets the value of the workouts property.
     * 
     * @param value
     *     allowed object is
     *     {@link WorkoutsT }
     *     
     */
    public void setWorkouts(WorkoutsT value) {
        this.workouts = value;
    }

    /**
     * Gets the value of the courses property.
     * 
     * @return
     *     possible object is
     *     {@link CoursesT }
     *     
     */
    public CoursesT getCourses() {
        return courses;
    }

    /**
     * Sets the value of the courses property.
     * 
     * @param value
     *     allowed object is
     *     {@link CoursesT }
     *     
     */
    public void setCourses(CoursesT value) {
        this.courses = value;
    }

}