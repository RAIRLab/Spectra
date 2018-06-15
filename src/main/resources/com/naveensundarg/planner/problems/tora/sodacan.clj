{:name       "soda can challenge"
 :background [  ;; If all the possible things are checked, then the answer is known.
               (forall [?thing] (if (and (Checked ?thing) (Possible ?thing))
                                  (exists ?answer (Know ?answer))))]
 :start      [;; A, B, and C are possible
              (Possible A)
              (Possible B)
              (Possible C)

              ;;; A, B, and C are the only possibilities
              (forall [?u] (if (and (not (= ?u A)) (not (= ?u B)) (not (= ?u C))) (not (Possible ?u))))]



 :actions    [ ;;; Checking something that is possible
               (define-action attend [?q]
                {:preconditions [(Possible ?q)]
                 :additions     [(Checked ?q)]
                 :deletions     []})]

 :goal [(exists ?answer (Know ?answer))]}