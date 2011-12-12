#include "jlg_btree.h"

static void *btree_objdup(void *srcp) {
	size_t s = strlen((char *) srcp) + 1;
	void *p = (void *) malloc(s);
	memmove(p, srcp, s);
	return p;
}

static size_t btree_str(void *src, char *dst, size_t size) {
	return strlcpy(dst, (char *) src, size);
}

static int btree_cmp(void *s1, void *s2) {
	return strcmp((char *) s1, (char *) s2);
}

btree_config_t *btree_config_create() {
	JLG_CREATE(p, btree_config_t);
	p->free_func = free;
	p->copy_func = btree_objdup;
	p->str_func = btree_str;
	p->cmp_func = btree_cmp;
	return p;
}

int btree_config_delete(btree_config_t **cfgpp) {
	JLG_FREE(cfgpp);
	return 0;
}

btree_node_t *btree_node_create(void *value, bool copy, btree_config_t *cfg) {
	JLG_CREATE(p, btree_node_t);
	p->left = NULL;
	p->right = NULL;
	if (copy) {
		p->value = cfg->copy_func(value);
	} else {
		p->value = value;
	}
	return p;
}

int btree_node_delete(btree_node_t **nodepp, btree_config_t *cfg) {
	if (nodepp && *nodepp) {
		btree_node_t *nodep = *nodepp;
		btree_node_delete(&(nodep->left), cfg);
		btree_node_delete(&(nodep->right), cfg);
		cfg->free_func(nodep->value);
	}
	JLG_FREE(nodepp);
	return 0;
}


// create a hash table with key size optionally specified
// this function allocates memory so hash_delete has to be called to free memory
btree_t *btree_create() {
	return btree_create_ex(btree_config_create());
}

btree_t *btree_create_ex(btree_config_t *cfg) {
	JLG_CREATE(p, btree_t);
	p->cfg = cfg;
	p->root = NULL;
	return p;
}


int btree_delete(btree_t **btreepp) {
	if (btreepp && *btreepp) {
		btree_t *btreep = *btreepp;
		btree_node_delete(&(btreep->root), btreep->cfg);
		btree_config_delete(&(btreep->cfg));
		ll_delete(&(btreep->listp));
	}
	JLG_FREE(btreepp);
	return 0;
}

void btree_free(void *btreep) {
	btree_delete(&btreep);
}

static int btree_node_add(btree_node_t **nodepp, void *value, bool copy, btree_config_t *cfg) {
	if (*nodepp == NULL) {
		*nodepp = btree_node_create(value, copy, cfg);
	} else {
		btree_node_t *nodep = *nodepp;
		int cmp = cfg->cmp_func(value, nodep->value);
		if (cmp == 0) {
			// replace the value
			if (copy) {
				cfg->free_func(nodep->value);
				nodep->value = cfg->copy_func(value);
			} else {
				nodep->value = value;
			}
		} else if (cmp < 0) {
			return btree_node_add(&(nodep->left), value, copy, cfg);
		} else {
			return btree_node_add(&(nodep->right), value, copy, cfg);
		}
	}
	return 0;
}

static int btree_add(btree_t *btreep, void *value, bool copy) {
	return btree_node_add(&(btreep->root), value, copy, btreep->cfg);
}

int btree_set(btree_t *btreep, void *value) {
	return btree_add(btreep, value, true);
}

int btree_put(btree_t *btreep, void *value) {
	return btree_add(btreep, value, false);
}

static int btree_node_get(btree_node_t *nodep, void *value_key, void **valuepp, btree_config_t *cfg) {
	if (nodep == NULL) {
		return BTREE_ERR_NOT_FOUND;
	}
	int cmp = cfg->cmp_func(value_key, nodep->value);
	if (cmp == 0) {
		*valuepp = nodep->value;
		return 0;
	} else if (cmp < 0) {
		return btree_node_get(nodep->left, value_key, valuepp, cfg);
	} else {
		return btree_node_get(nodep->right, value_key, valuepp, cfg);
	}
}

int btree_get(btree_t *btreep, void *value_key, void **valuepp) {
	return btree_node_get(btreep->root, value_key, valuepp, btreep->cfg);
}

int btree_get_next(btree_t *btreep, void *value_key, void **valuepp) {
	// O(N) so can be improved in O(log2(N))
	*valuepp = NULL;
	ll_node_t *nodep = btree_ordered_list(btreep)->first;
	if (nodep) {
		*valuepp = nodep->valuep;
	} else {
		return BTREE_ERR_EMPTY;
	}
	while (nodep) {
		int cmp = btreep->cfg->cmp_func(value_key, nodep->valuep);
		if (cmp < 0) {
			*valuepp = nodep->valuep;
			break;
		}
		nodep = nodep->nextp;
	}	
	return 0;
}
int btree_get_previous(btree_t *btreep, void *value_key, void **valuepp) {
	// O(N) so can be improved in O(log2(N))
	*valuepp = NULL;
	ll_node_t *nodep = btree_ordered_list(btreep)->first;
	if (!nodep) {
		return BTREE_ERR_EMPTY;
	}
	bool go_to_last = false;
	ll_node_t *previous_nodep = NULL;
	while (nodep) {
		int cmp = btreep->cfg->cmp_func(value_key, nodep->valuep);
		if ((go_to_last == false) && (cmp <= 0)) {
			if (previous_nodep) {
				*valuepp = previous_nodep->valuep;
				break;
			} else {
				go_to_last = true;
			}
		}
		previous_nodep = nodep;
		nodep = nodep->nextp;
	}
	if (*valuepp == NULL) {
		*valuepp = previous_nodep->valuep;
	}
	return 0;	
}

bool btree_contains(btree_t *btreep, void *value_key) {
	void *value = NULL;
	if (btree_get(btreep, value_key, &value) == BTREE_ERR_NOT_FOUND) {
		return false;
	} else {
		return true;
	}
}

static int btree_node_remove(btree_node_t **nodepp, void *value_key, btree_config_t *cfg) {
	if (nodepp == NULL) {
		return 1;
	}
	if (*nodepp == NULL) {
		return BTREE_ERR_NOT_FOUND;
	}
	btree_node_t *nodep = *nodepp;
	int cmp = cfg->cmp_func(value_key, nodep->value);
	if (cmp == 0) {
		// remove the value
		cfg->free_func(nodep->value);
		nodep->value = NULL;
		if ((nodep->left == NULL) && (nodep->right == NULL)) {
			// case 0 child : remove the node
			btree_node_delete(nodepp, cfg);
		} else if (nodep->left == NULL) {
			// case 1 child : replace the node by the child node
			btree_node_t *noderightp = nodep->right;
			nodep->value = noderightp->value;
			nodep->left = noderightp->left;
			nodep->right = noderightp->right;
			free(noderightp);
		} else if (nodep->right == NULL) {
			btree_node_t *nodeleftp = nodep->left;
			nodep->value = nodeleftp->value;
			nodep->left = nodeleftp->left;
			nodep->right = nodeleftp->right;
			free(nodeleftp);
		} else {
			// case 2 children : V is the value to remove
			// 1) find the next "in order" value on the right called R (or the previous "in order" value on the left)
			// 2) replace V by R in the node
			// 3) remove the node R
			btree_node_t *next_nodep = nodep->right;
			btree_node_t *parent_nodep = nodep;
			while (next_nodep->left) {
				parent_nodep = next_nodep;
				next_nodep = next_nodep->left;
			}
			nodep->value = next_nodep->value;
			if (parent_nodep == nodep) {
				parent_nodep->right = next_nodep->right;
			} else {
				parent_nodep->left = next_nodep->right;
			}
			free(next_nodep);
		}
	} else if (cmp < 0) {
		btree_node_remove(&(nodep->left), value_key, cfg);
	} else {
		btree_node_remove(&(nodep->right), value_key, cfg);
	}
	return 0;
}

int btree_remove(btree_t *btreep, void *value_key) {
	int status = btree_node_remove(&(btreep->root), value_key, btreep->cfg);
	return status;
}

static int btree_node_to_string(btree_node_t *nodep, char *dst, size_t size, btree_config_t *cfg) {
	if (nodep == NULL) {
		snprintf(dst, size, "-");
	} else {
		// <node_value>(left, right)
		char value_buffer[BUFFER_SIZE] = "";
		cfg->str_func(nodep->value, value_buffer, BUFFER_SIZE);
		
		char left_buffer[BUFFER_SIZE] = "";
		btree_node_to_string(nodep->left, left_buffer, BUFFER_SIZE, cfg);
		char right_buffer[BUFFER_SIZE] = "";
		btree_node_to_string(nodep->right, right_buffer, BUFFER_SIZE, cfg);
		
		
		snprintf(dst, size, "%s(%s, %s)", value_buffer, left_buffer, right_buffer);
	}
	return 0;
}


int btree_to_string(btree_t *btreep, char *dst, size_t size) {
	// <node_value>(left, right)
	dst[0] = '\0';
	return btree_node_to_string(btreep->root, dst, size, btreep->cfg);
}

static int btree_node_ordered_list(btree_node_t *nodep, linked_list_t *listp) {
	if (nodep == NULL) {
		return 0;
	}
	btree_node_ordered_list(nodep->right, listp);
	ll_push(listp, nodep->value);
	btree_node_ordered_list(nodep->left, listp);
	return 0;
}

linked_list_t *btree_ordered_list(btree_t *btreep) {
	ll_delete(&(btreep->listp));
	btreep->listp = ll_create();
	btree_node_ordered_list(btreep->root, btreep->listp);
	return btreep->listp;
}

