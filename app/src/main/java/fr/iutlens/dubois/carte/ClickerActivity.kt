package fr.iutlens.dubois.carte

//Ici tout les imports, voila
import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

class ClickerActivity : AppCompatActivity() {

    val images =arrayOf(R.drawable.ordi_bras_droite, R.drawable.ordi_bras_gauche)

    /*
    * -----------------------------------------------------------
    * Variables mini-jeux
    *   Mini-jeu 1
    * -----------------------------------------------------------
    */
    //La variable ajout est en gros la valeur du vertical bias de la flèche. Cette dernière commence à 0.85, ce qui correspond au rouge sur la barre
    private var ajout=0.85f
    //Variable qui ne sert à rien pour l'instant, mais on la garde car on sais jamais
    //private var timer: Int=1
    //La variable diff est la valeur avec laquelle le vertical bias vas être ajouté, en gros "la force du click".
    private var diff=0.0015f


    /*
    * -----------------------------------------------------------
    * Variables mini-jeux
    *   Mini-jeu 2
    * -----------------------------------------------------------
    */
    //A la même fonction que ajout dans le mini-jeu 1. Le vertical bias commence à 0.99, tout en bas de la barre
    private var ajoutmj2=0.99f
    //La variable winCond qui permet de déterminer si le jeu est encore
    private var winCond: Int=1
    //Variable click pour enregistrer le nombre de click que le joueur a fait, pour les animations
    private var click: Int=0

    //private var imgUri= Uri.parse("android.ressource://my.")

    /*
     * -----------------------------------------------------------
     * Variables mini-jeux
     *   Mini-jeu 3
     * -----------------------------------------------------------
     */


    //Détermination de quelle variante du clicker vas être jouée.
    //Le nombre de variantes au clicker est 3 mais peut être augmenté tant que l'on alimente le when et que l'on crée la fonction adéquate
    private var minrdm: Int=1
    private var maxrdm: Int=2

    private var res: Int=rdm(minrdm,maxrdm)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clicker)

        //Ici un when pour facilement déterminer quel variante vas être mise. J'ai fais ce système pour éviter que le code ne ressemble à rien avec tous les commentaires.
        when (res) {
            1->minijeu1()
            2->minijeu2()
        }
    }

    /*
    * -----------------------------------------------------------
    * Fonctions des mini-jeux (Fonctions principales), vitales
    * -----------------------------------------------------------
    */

    //Mini-jeu 1: Résister à la fatigue en cliquant le plus vite possible pendant 10 secondes. Plus le temps passe, plus cela deviens dur à passer
    private fun minijeu1() {
        click=0
        winCond=1

        val imageView : ImageView = findViewById<ImageView>(R.id.code_face);

        //Variable adder pour trouver le bouton...
        val adder=findViewById<ImageView>(R.id.code_face)
        //...pour ensuite ajouter un écouteur dessus. Un écouteur qui vas écouter les clicks sur le bouton
        adder.setOnClickListener {
            click++
            //On ajoute un if pour checker le vertical bias de la flèche. Pour rappel, ajout correspond au vertical bias de la flèche
            //Si ajout est compris entre 0.02 et 1, alors on peut clicker pour faire baisser le verticalbias de la flèche.
            if (ajout>=0.02 && ajout<1) {
                //On fais monter la flèche en faisant baisser le vertical bias de cette dernière
                 ajout-=0.02f
                //La fonction est plus en-dessous.
                 progres(ajout)
            } else {
                winCond=0
            }
            //Si la flèche est trop haute (ajout>0.02) alors on la laisse descendre.
            //La flèche ne remonte plus quand on clicke tant que ajout est au-dessus de 0.02.

        }

        //Voici maintenant la fonction qui fait baisser la flèche de manière continue
        //la valeur mainHandler est une fonction qui permet de faire des bloucles, mais à l'infini puisqu'il n'y a pas de conditions de fin comme on peut trouver sur un for ou sur un while.
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                //On effectue une délay toutes les 10 milisecondes, et donc d'effectuer une action toutes les 10 milisecondes.
                mainHandler.postDelayed(this, 10)
                //Ici iun findviewbyid pour débeuger et pour voir la valeur du vertical bias
                //findViewById<TextView>(R.id.test1).text=ajout.toString()
                if (ajout>=1) { //Si le vertical bias est en-dessous de 1 (=si la flèche est trop basse)
                    //Alors on ne peut plus clicker et le titre change.
                    title="Tu t'es endormi !"
                    findViewById<TextView>(R.id.temps).setText("Tu t'es endormi ! Perdu !").toString()
                    imageView.setImageResource(R.drawable.ordi_win)

                } else {
                    //Si on est toujours dans les conditions de victoire (la flèche n'est pas trop basse)
                    //Alors on diminue le vertical bias de la flèche pour la faire monter
                    ajout+=diff
                    //Plus de détails sur la fonction en-bas
                    progres(ajout)

                    imageView.setImageResource(images[click % images.size])
                }
            }
        })

        /*"Mais pourquoi on check sur 2 méthodes différentes si la flèche est trop basse ? (sur le click et toutes les 10 ms)"
        * Car la première méthode chècke si on peut faire une action quand on clicke, et si on suit cette logique cela veut dire que l'on ne peut jamais perdre si la flèche est trop basse et qu'on le clique pas.
        * Et c'est donc pourquoi on a ajouté une seconde méthode qui, en plus de faire continuellement baisser la flèche, check si cette dernière n'est pas trop basse et donc blocker ou non l'action de baiser le vertical bias de flèche.*/

        //Ici le timer que on vas utiliser pour faire afficher le temps restant au clicker. Le timer sure 10 secondes. Les temps ici sont mesuérés avec des ms et donc (temps en ms)/1000 donne le temps en secondes.
        object : CountDownTimer(10000, 10) {
            //Chose important à noter: la varibale "millisUntilFinished" est la variable qui permet de dire en ms combien de temps il reste au timer.
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                //On cherche le texte qui s'appelle "remaining" pour lui faire afficher le nombres de secondes restantes. Si vous voulez faire changer la textView, touchez pas à "millisUntilFinished / 1000", sinon ça casse tout.
                //findViewById<TextView>(R.id.remaining).text = "Il reste "+millisUntilFinished/1000+"secs"
                //findViewById<TextView>(R.id.test2).text=diff.toString()
                //Ce bout de code avec les if/else if sert à augmenter la vitesse de la flèche qui descend en fonction du temps. On vas faire un exemple avec le premier:
                //Si il reste entre 9s et 6s, alors la flèche voir son vertical bias modifié de 0.001 toutes les 10 ms.
                //Toutes les 10 ms car c'est ce qu'on a décidé au-dessus avec le handleur et le délai.
                //Etc, etc...

                //Toast.makeText(ClickerActivity, "Il reste "+millisUntilFinished/1000+" s")

                when (millisUntilFinished) {
                    in(6000..8999) -> diff=0.001f
                    in(5000..5999) -> diff=0.002f
                    in(3000..4999) -> diff=0.0035f
                    in(1500..2999) -> diff=0.0045f
                    in(50..1499) -> diff=0.006f
                }

                if(millisUntilFinished<=30) {
                    ajout += -ajout
                    diff += -diff
                    progres(ajout)
                    imageView.setImageResource(R.drawable.ordi_win)
                }

                findViewById<TextView>(R.id.temps).setText(""+millisUntilFinished / 1000+" s").toString()
            }

            //Ici une fonction pour dire ce qu'on vas faire quand le timer est fini. Idéalement retourner sur la carte mais j'ai tenté mais ça marche pas :/.
            override fun onFinish() {
                /*Sauvegarder dans un fichier préférences pour passer des varibales dans plusieurs activités.*/
                winCond=0
                imageView.setImageResource(R.drawable.ordi_win)
                hey()

            }
            //.start pour commencer le timer.
        }.start()

        imageView.setImageResource(R.drawable.ordi_win)

    }



    //Mini-jeu dans lequel on doit tapper le plus dans un temps imparti pour coder quelque chose. On vas utiliser le même système que le mini-jeu 1
    private fun minijeu2() {
        //findViewById<ImageView>(R.id.code_face).setVisibility(View.GONE)


        //findViewById<TextView>(R.id.textView3).setText("AAAAAAAAAAAAAAAAAAAAAAAAAAA"+screenSize.heightPixels.toString())

        //Première chose à faire est d'initialiser où est-ce-que la flèche noire vas se trouver dès le chargement, et ça on le fais avec progress et on le met à 0.99 car cela correspond à la flèche étant tout en bas de la barre
        progres(0.99f)
        //Ligne pour débeuger, voir si adder marche bien findViewById<TextView>(R.id.test1).setText(ajoutmj2.toString())
        //Ici une ligne pour faire disparaître le texte, car nous n'en n'avons pas besoin.
        //findViewById<TextView>(R.id.test1).setText("")
        title="Clique pour coder un maximum !"
        //On demande à trouver le bouton...
        val adder=findViewById<ImageView>(R.id.code_face)
        //...pour y ajouter un écouteur qui vas détecter les clicks
        adder.setOnClickListener {
            click++

            //findViewById<TextView>(R.id.textView3).setText(click.toString()+" "+images.size.toString()+" "+click % images.size)

            if (ajoutmj2>0.02 && winCond==1) {
                //Si la flèche n'est pas tout en haut de la barre, alors on a la possibilité de la faire baisser
                ajoutmj2-=0.01f
                progres(ajoutmj2)
            }

            val imageView : ImageView = findViewById<ImageView>(R.id.code_face);
            imageView.setImageResource(images[click % images.size])
        }

        //Partie du timer. C'est le même système que le premier mini-jeu: un timer qui dure 12secondes et qui se met à jour toutes les 10ms.
        object : CountDownTimer(12000, 10) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                //Quand le timer est en train de tourner:
                    //On met à jour le texte qui indique le temps restant
                findViewById<TextView>(R.id.temps).setText(""+millisUntilFinished / 1000+" s").toString()
                    //On met à jour continuellement le score pour éviter des bugs type le dernier clic n'est pas pris en compte.
                //findViewById<TextView>(R.id.test1).setText("Score: "+(100-(ajoutmj2*100)).toInt()+"/100").toString()
            }

            //Ici on affiche le score final. Chose à ajouter: après x secondes, retourner à la carte. Pendant ce délay des X secondes, afficher en grand le score final
            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                //Je met à jour le titre et le textView pour afficher le score final.
                title="Score final: "+(100-(ajoutmj2*100)).toInt()+"/100"
                //findViewById<TextView>(R.id.test1).setText("Score final: "+(100-(ajoutmj2*100)).toInt()+"/100").toString()
                //Je met à jour la winCondition, pour éviter que le joueur puisse clicker et modifier son score final même quand le timer est fini
                winCond=0
                //Et je fais disparaitre le timer puisqu'il ne sert plus à rien ici.
                //findViewById<TextView>(R.id.remaining).setText("").toString()

                val finalgrade: Int=(100-(ajoutmj2*100)).toInt();

                val sharedPref = this@ClickerActivity.getSharedPreferences(
                    "notes", Context.MODE_PRIVATE)
                with (sharedPref.edit()) {
                    putInt("clicker", finalgrade);
                    apply()
                }

                test(finalgrade)
            }
        }.start()

    }

    /*
    * -----------------------------------------------------------
    * Fonctions diverses mais toujours vitales
    * -----------------------------------------------------------
    */

    private fun rdm(min:Int,max:Int): Int {
        return (min..max).random()
    }

    //Et la fonction pour à la fois récupérer la valeur du vertical bias de la flèche mais aussi pour faire aumgmenter/diminuer le vertical bias.
    //La fonction prend en paramètre un nombre flotant, car le vertical bias est une valeur comprise entre 0 et 1.
    //FONCTION TRES IMPORTANTE, A NE PAS SUPR SOUS PEINE DE DEVOIR PORTER UN SOMBRERO LE 21 FEVRIER.
    private fun progres(fl: Float) {
        //On crée un nouveau constrainSet
        val set = ConstraintSet()
        //On demande de trouver la flèche (findViewById) et de récupérer toutes ses contraintes (bottomOf, topOf, Parent, etc...). Ce qui nous intéresse ici c'est le vertical bias.
        val constraintLayout = findViewById<ConstraintLayout>(R.id.img_face)
        //On fais un clone temporaire de la flèche avec toutes ses contraintes
        set.clone(constraintLayout)

        //On fais ce que l'on veut au clone de la flèche. Ici, on vas modifier son vertical bias et vas prendre en valeur fl qui est la veleur que le Vertical bias vas prendre.
        set.setVerticalBias(R.id.dark_arrow_clicker, fl)
        //Et enfin on remplace la flèche par son clone avec toutes les nouvelles modifications
        set.applyTo(constraintLayout)
    }

    private fun test(jaaj: Int) {
        object : CountDownTimer(3000, 10) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                findViewById<TextView>(R.id.temps).setText("Score final: "+jaaj.toString()+" /100").toString()
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                this@ClickerActivity.finish()
            }
        }.start()
    }

    private fun hey() {
        object : CountDownTimer(3000, 10) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                findViewById<TextView>(R.id.temps).setText("Tu as réussi à ne pas t'endormir !").toString()
                title="Tu as réussi à ne pas t'endormir !"
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                this@ClickerActivity.finish()
            }
        }.start()
    }
}
