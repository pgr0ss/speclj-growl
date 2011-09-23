(ns speclj.report.growl_spec
  (:use
    [speclj.core]
    [speclj.exec :only (pass-result fail-result pending-result)]
    [speclj.report.growl]
    [speclj.reporting :only (report-runs)]))


(describe "Growl Reporter"
  (with reporter (new-growl-reporter))
  (with notification (atom nil))
  (with title (atom nil))
  (with message (atom nil))
  (with fake-growl (fn [_notification _title _message]
                     (reset! @notification _notification)
                     (reset! @title _title)
                     (reset! @message _message)))

  (describe "report-runs"
    (it "prints summary to the terminal"
        (binding [growl @fake-growl]
          (let [output (with-out-str (report-runs @reporter []))]
            (should (.contains output "0 examples, 0 failures")))))

    (it "growls summary information for no test runs"
        (binding [growl @fake-growl]
          (let [output (with-out-str (report-runs @reporter []))]
            (should= "Message" @@notification)
            (should= "Specs" @@title)
            (should= "0 examples, 0 failures" @@message))))

    (it "growls a successful run"
        (binding [growl @fake-growl]
          (let [result1 (pass-result nil 0.1)
                result2 (pass-result nil 0.02)
                results [result1 result2]
                output (with-out-str (report-runs @reporter results))]
            (should= "Message" @@notification)
            (should= "Specs" @@title)
            (should= "2 examples, 0 failures" @@message))))))
