;; j is an A
;; all A are afraid of B
;; what is j afraid of?

(seq P 2 3 A)
(seq Q 2 3 B)
(seq all A are afraid of M)
(seq all B are afraid of N)
(Goal (seq What is P afraid of?))
;;;
(action A)

(if (and (seq ?individual is a ?class1)
         (seq all ?class1 are afraid of ?class2)
         (Goal (seq what is ?individual afraid of)))
  (Action ?class2))

;;; given background B,  goal G and plan P=>
;;  let sub(B) be all the formulae containing individuals in G and P. And let it be closed wrt to inviduals.
;;  then (if (and sub(B) G) variablize(P)) is a hypothesis.