(defproject speclj-growl "2.0.0"
  :description "Growl reporter for the speclj testing framework"
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [gntp "0.6.0"]]
  :resource-paths ["resources"]
  :profiles {
    :dev {
      :plugins [[speclj "2.5.0"]]
      :dependencies [[speclj "2.5.0"]]
      :test-paths ["spec/"]}})
