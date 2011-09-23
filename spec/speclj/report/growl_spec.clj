(ns speclj.report.growl_spec
  (:use
    [speclj.core]
    [speclj.components :only (new-characteristic new-description)]
    [speclj.exec :only (pass-result fail-result pending-result)]
    [speclj.report.growl]
    [speclj.report.documentation :only (new-documentation-reporter)]
    [speclj.reporting :only (report-description report-fail report-message report-pass report-pending report-runs)])
  (:import
    [speclj SpecFailure SpecPending]))


(describe "Growl Reporter"
  (with documentation-reporter (new-documentation-reporter))
  (with reporter (new-growl-reporter))
  (with notification (atom nil))
  (with title (atom nil))
  (with message (atom nil))
  (with fake-growl (fn [_notification _title _message]
                     (reset! @notification _notification)
                     (reset! @title _title)
                     (reset! @message _message)))

  (describe "report-message"
    (with description (new-description "Verbosity" *ns*))
    (it "prints the same messages as DocumentationReporter to the terminal"
      (let [expected-output (with-out-str (report-message @documentation-reporter "message"))]
        (should= expected-output (with-out-str (report-message @reporter "message"))))))

  (describe "report-description"
    (it "prints the same messages as DocumentationReporter to the terminal"
      (let [description (new-description "Desc" *ns*)
            expected-output (with-out-str (report-description @documentation-reporter description))]
        (should= expected-output (with-out-str (report-description @reporter description))))))

  (describe "report-pass"
    (it "prints the same messages as DocumentationReporter to the terminal"
      (let [description (new-description "Desc" *ns*)
            characteristic (new-characteristic "says pass" description "pass")
            result (pass-result characteristic 1)
            expected-output (with-out-str (report-pass @documentation-reporter result))]
        (should= expected-output (with-out-str (report-pass @reporter result))))))

  (describe "report-pending"
    (it "prints the same messages as DocumentationReporter to the terminal"
      (let [characteristic (new-characteristic "pending" `(pending))
            result (pending-result characteristic 1 (SpecPending. "some reason"))
            expected-output (with-out-str (report-pending @documentation-reporter result))]
        (should= expected-output (with-out-str (report-pending @reporter result))))))

  (describe "report-fail"
    (it "prints the same messages as DocumentationReporter to the terminal"
      (let [description (new-description "Desc" *ns*)
            characteristic (new-characteristic "says fail" description "fail")
            result (fail-result characteristic 2 (SpecFailure. "blah"))
            expected-output (with-out-str (report-fail @documentation-reporter result))]
        (should= expected-output (with-out-str (report-fail @reporter result))))))

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
