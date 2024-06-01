
;This is Brandon's grapevine problem from the SPECTRA system description paper (https://doi.org/10.1007/s13218-024-00847-8)

{:name "GrapeVine"
    :background [
        ; Type predicates
        (agent a)
        (agent b)
        (agent c)
        (room p1)
        (room p2)

        ; Unique name axioms
        (not (= a b))
        (not (= a c))
        (not (= b c))
    ]
    :actions [
        (define-action left [?a] {
            :preconditions [
                (agent ?a) ; Type restriction
                (at ?a p2)
            ]
            :additions [
                (at ?a p1)
                (not (at ?a p2))
            ]
            :deletions [
                (not (at ?a p1))
                (at ?a p2)
            ]
        })


        (define-action right [?a] {
            :preconditions [
                (agent ?a) ; Type restriction
                (at ?a p1)
            ]
            :additions [
                (at ?a p2)
                (not (at ?a p1))
            ]
            :deletions [
                (not (at ?a p2))
                (at ?a p1)
            ]
        })

        (define-action share-both [?a1 ?a2 ?a3 ?r] {
            :preconditions [
                ; Type restrictions
                (agent ?a1)
                (agent ?a2)
                (agent ?a3)
                (room ?r)
                ; Precondition
                (at ?a1 ?r)
                (at ?a2 ?r)
                (at ?a3 ?r)
                (not (= ?a1 ?a2))
                (not (= ?a1 ?a3))
                (not (= ?a2 ?a3))
            ]
            :additions [
                (Believes! ?a2 (the ?a1))
                (Believes! ?a3 (the ?a1))
                (Believes! ?a1 (Believes! ?a2 (the ?a1)))
                (Believes! ?a1 (Believes! ?a3 (the ?a1)))
            ]
            :deletions [
                (not (Believes! ?a2 (the ?a1)))
                (not (Believes! ?a3 (the ?a1)))
                (not (Believes! ?a1 (Believes! ?a2 (the ?a1))))
                (not (Believes! ?a1 (Believes! ?a3 (the ?a1))))
            ]
            :cost 2
        })

        (define-action share-single [?a1 ?a2 ?a3 ?r] {
            :preconditions [
                ; Type restrictions
                (agent ?a1)
                (agent ?a2)
                (agent ?a3)
                (room ?r)
                ; Precondition
                (at ?a1 ?r)
                (at ?a2 ?r)
                (not (at ?a3 ?r))
                (not (= ?a1 ?a2))
                (not (= ?a1 ?a3))
                (not (= ?a2 ?a3))
            ]
            :additions [
                (Believes! ?a2 (the ?a1))
                (Believes! ?a1 (Believes! ?a2 (the ?a1)))
            ]
            :deletions [
                (not (Believes! ?a2 (the ?a1)))
                (not (Believes! ?a1 (Believes! ?a2 (the ?a1))))
            ]
            :cost 2
        })

    ]
    :start [
        ; Locations
        (at a p1)
        (not (at a p2))
        (at b p1)
        (not (at b p2))
        (at c p1)
        (not (at c p2))

        ; Each agent has a secret
        (Believes! a (the a))
        (Believes! b (the b))
        (Believes! c (the c))

        ; No one believes a's secret
        (not (Believes! b (the a)))
        (not (Believes! c (the a)))
    ]
    :goal [
        (Believes! b (the a))
        (Believes! a (Believes! b (the a)))
        (not (Believes! c (the a)))
    ]

}






