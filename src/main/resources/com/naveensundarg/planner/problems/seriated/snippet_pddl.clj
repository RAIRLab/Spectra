{


 :background [

              ;;; Sizes of cups
              (< (size (cup 1)) (size (cup 1)))
              (< (size (cup 1)) (size (cup 2)))
              (< (size (cup 1)) (size (cup 3)))



              (< (size (cup 99)) (size (cup 100)))





              ]
 }


(exist [?y] (and (isColor ?y PURPLE)
                 (isShape ?y CUBE)))

(cup 1)
(cup 2)


(cup 100)



:background [ ;; Transitivity of <
             (forall [?x ?y ?z]
                     (if (and (< (size ?x) (size ?y))
                              (< (size ?y) (size ?z)))
                       (< (size ?x) (size ?z))))
             ;; Asymmetry of <
             (forall [?x ?y]
                     (iff (< (size ?x) (size ?y))
                          (not (< (size ?y) (size ?x)))))

             ;; If there is something inside a cup, it is not empty.
             (forall [?y]
                     (if (exists [?x] (In ?x ?y))
                       (not (Empty ?y))))

             ;;; Sizes of cups
             :ARITHMETIC_AXIOMS

             (forall [?number] (= number? (size (cup ?number))))

             ]

