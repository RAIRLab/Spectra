{:name       "soda can challenge 2"
 :background [ ;; Membership in a list
               (forall [?u ?l]
                      (iff (member ?u ?l)
                           (exists [?v ?rest]
                                   (and (= ?l (cons ?v ?rest))
                                        (or (= ?u ?v)
                                            (member ?u ?rest))))))
              ;; Empty list
              (forall ?x (not (member ?x el)))
              ;; Removal clause 1
               (forall [?item] (= (remove ?item el) el))
              ;; Removal clause 2
               (forall [?item ?rest]
                       (= (remove ?item (cons ?item ?rest))
                          (remove ?item ?rest)))
               ;; Remove clause 3
               (forall [?item ?head ?rest]
                       (if
                         (not (= ?head ?item))
                         (= (remove ?item (cons ?head ?rest))
                            (cons ?head (remove ?item ?rest)))))]

 :start      [(state (cons a (cons b (cons c el))))
              (and (not (= a b))
                   (not (= b c))
                   (not (= c a))
                   (not (= a el))
                   (not (= b el))
                   (not (= c el)))]

 :goal       [(attend a)
              (attend b)
              (attend c)]

 :actions    [;; checkbehind an object
               (define-action attend [?item ?list]
                 {:preconditions [(and (state ?list) (member ?item ?list))]
                  :additions     [(attend ?item)
                                  (state (remove ?item ?list))]
                  :deletions     [(state ?list)]})]}
