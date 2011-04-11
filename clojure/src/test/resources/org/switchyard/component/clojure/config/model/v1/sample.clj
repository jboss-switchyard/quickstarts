(ns org.switchyard (:import org.switchyard.Exchange)) 
(defn process [ex] (.setContent (.getMessage ex) "Fletch") 
  (.getContent (.getMessage ex)))