(defproject speclj-growl "2.0.0"
  :description "Growl reporter for the speclj testing framework"
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [gntp "0.6.0"]]
  :resource-paths ["resources"]
  :url "https://github.com/pgr0ss/speclj-growl"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :profiles {
    :dev {
      :plugins [[speclj "2.5.0"]]
      :dependencies [[speclj "2.5.0"]]
      :test-paths ["spec/"]}})
