Rules to code with JLG Library

Coding like object programming.
We try to do:
- one c file per "class" (and its header file)

Instanciating an object:
object_t *objectp = object_create(); // memory dynamically allocated
...
object_delete(&objectp); // memory freed

All the object are struct containing:
fixed object (int, char [BUFFER_SIZE], etc.)
pointer on other object : char *, subobject_t *, link_t *, ...

pointer object are considered to be part of the object
except when they are "link" (not under the responsability of the object)
so they have to be freed when freeing the object. (in the delete method)

# Accessors
set : copy the value and attach the copy to the object. the copy is under the responsability of the object.
put : do not copy the value and attach the original to the object. the value is under the responsability of the object.
link : do not copy the value and attach the original to the object. the value is however not under the responsability of the object.

set template : JLG_FREE(member) + malloc
put template : JLG_FREE(member) + =
link template : no need accessor (see hash_t case with create_ex)

get


DHT
Implementation la plus simple possible quite à etre la plus naive et
sans soucis de performance ou de securite

Node ID: choisi au hasard par l'agent
Un agent doit connaitre tous les autres agents avec leur responsabilite.
Un agent est responsable d'une cle lorsque c'est lui qui a le node_id le plus proche de la clé parmi tous les agents.
Les clés et node_id sont dans le meme ensemble (entre 0 et 2^L L=64, 128 ou 160...). Il y a donc une relation d'ordre
total entre les clé et on peut parler de distance entre deux elements de l'ensemble.

